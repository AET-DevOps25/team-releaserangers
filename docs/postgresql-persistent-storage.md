# PostgreSQL Persistent Storage Setup

This document describes how to set up and use the EBS volume for PostgreSQL persistent storage.

## Overview

The Terraform configuration creates an encrypted EBS volume that is attached to the EC2 instance for PostgreSQL data persistence. The volume is attached at `/dev/sdf` and needs to be formatted and mounted before use.

## EBS Volume Configuration

The following variables control the EBS volume configuration in `terraform.tfvars`:

- `ebs_volume_size`: Size of the EBS volume in GB (default: 20)
- `ebs_volume_type`: Type of EBS volume - gp3, gp2, io1, io2, st1, sc1 (default: gp3)
- `ebs_volume_encrypted`: Whether to encrypt the volume (default: true)

## Manual Setup Instructions

After running `terraform apply`, the EBS volume will be attached to the EC2 instance but needs to be formatted and mounted:

### 1. Connect to the EC2 Instance

```bash
ssh -i /path/to/your/private-key.pem ubuntu@<instance-ip>
```

### 2. Check Attached Volume

```bash
sudo lsblk
# You should see the attached volume, typically as /dev/nvme1n1 or /dev/xvdf
```

### 3. Format the Volume (First Time Only)

**⚠️ Warning: This will erase all data on the volume. Only run this on a new, empty volume.**

```bash
# Check if the volume has a filesystem
sudo file -s /dev/nvme1n1

# If it shows "data", the volume is empty and needs formatting
sudo mkfs -t ext4 /dev/nvme1n1
```

### 4. Create Mount Point and Mount the Volume

```bash
# Create mount point for PostgreSQL data
sudo mkdir -p /var/lib/postgresql-data

# Mount the volume
sudo mount /dev/nvme1n1 /var/lib/postgresql-data

# Set appropriate ownership and permissions
sudo chown -R 999:999 /var/lib/postgresql-data
sudo chmod 755 /var/lib/postgresql-data
```

### 5. Make Mount Persistent

Add the mount to `/etc/fstab` to ensure it persists across reboots:

```bash
# Get the UUID of the volume
sudo blkid /dev/nvme1n1

# Add to /etc/fstab (replace UUID with actual UUID from previous command)
echo 'UUID=your-volume-uuid /var/lib/postgresql-data ext4 defaults,nofail 0 2' | sudo tee -a /etc/fstab
```

### 6. Configure PostgreSQL to Use the Persistent Volume

Update your Docker Compose configuration to mount the persistent volume:

```yaml
postgres-db:
  image: postgres:15-alpine
  environment:
    POSTGRES_USER: release
    POSTGRES_PASSWORD: ranger
    POSTGRES_DB: devops25_db
  volumes:
    - /var/lib/postgresql-data:/var/lib/postgresql/data
  ports:
    - "5432:5432"
```

## Automated Setup with Ansible

For automated setup, you can extend the Ansible playbook to handle the EBS volume mounting:

```yaml
- name: Format EBS volume for PostgreSQL
  filesystem:
    fstype: ext4
    dev: /dev/nvme1n1
  become: yes
  when: not ansible_mounts | selectattr('device', 'equalto', '/dev/nvme1n1') | list

- name: Create PostgreSQL data directory
  file:
    path: /var/lib/postgresql-data
    state: directory
    mode: '0755'
  become: yes

- name: Mount PostgreSQL data volume
  mount:
    path: /var/lib/postgresql-data
    src: /dev/nvme1n1
    fstype: ext4
    opts: defaults,nofail
    state: mounted
  become: yes

- name: Set PostgreSQL data directory ownership
  file:
    path: /var/lib/postgresql-data
    owner: 999
    group: 999
    recurse: yes
  become: yes
```

## Benefits

- **Data Persistence**: Database data survives EC2 instance termination and recreation
- **Performance**: GP3 volumes provide consistent performance with 3,000 IOPS baseline
- **Security**: Volume is encrypted at rest using AWS managed keys
- **Scalability**: Volume size can be increased without downtime
- **Backup**: EBS snapshots can be used for point-in-time backups

## Troubleshooting

### Volume Not Visible
If the volume doesn't appear as expected:
- Check `sudo lsblk` for all attached volumes
- The device name might differ (e.g., `/dev/nvme1n1` instead of `/dev/xvdf`)

### Permission Issues
If PostgreSQL can't write to the volume:
- Ensure the directory is owned by user ID 999 (postgres user in container)
- Check that the mount point has appropriate permissions (755)

### Mount Failures
If mounting fails:
- Verify the volume is formatted with `sudo file -s /dev/nvme1n1`
- Check system logs with `sudo journalctl -u mount`