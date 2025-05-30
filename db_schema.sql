CREATE SCHEMA IF NOT EXISTS "public";

/* This table is only for temporary purposes. It should be replaced with a proper authentication system. */
CREATE TABLE IF NOT EXISTS "public"."users" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "email" "text" NOT NULL,
    "password" "text" NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "updated_at" timestamp with time zone DEFAULT "now"() NOT NULL
);

CREATE TABLE IF NOT EXISTS "public"."chapters" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "course_id" "uuid" NOT NULL,
    "content" "text" DEFAULT ''::"text" NOT NULL,
    "emoji" "text" DEFAULT '📄'::"text" NOT NULL,
    "is_favorite" boolean DEFAULT false NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "updated_at" timestamp with time zone DEFAULT "now"() NOT NULL
);


CREATE TABLE IF NOT EXISTS "public"."courses" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "user_id" "uuid" NOT NULL,
    "name" "text" NOT NULL,
    "description" "text" DEFAULT ''::"text" NOT NULL,
    "emoji" "text" DEFAULT '📖'::"text" NOT NULL,
    "is_favorite" boolean DEFAULT false NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "updated_at" timestamp with time zone DEFAULT "now"() NOT NULL
);


ALTER TABLE ONLY "public"."courses"
    ADD CONSTRAINT "Courses_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."chapters"
    ADD CONSTRAINT "chapters_pkey" PRIMARY KEY ("id");


ALTER TABLE ONLY "public"."chapters"
    ADD CONSTRAINT "chapters_course_id_fkey" FOREIGN KEY ("course_id") REFERENCES "public"."courses"("id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."courses"
    ADD CONSTRAINT "courses_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE "public"."chapters" ENABLE ROW LEVEL SECURITY;


ALTER TABLE "public"."courses" ENABLE ROW LEVEL SECURITY;

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_email_key" UNIQUE ("email");

CREATE INDEX IF NOT EXISTS "idx_chapters_course_id" ON "public"."chapters" ("course_id");
CREATE INDEX IF NOT EXISTS "idx_courses_user_id" ON "public"."courses" ("user_id");

/* Setup RLS if correct authentication is used 

-- RLS Policies for courses table
CREATE POLICY "Users can view their own courses" 
ON "public"."courses" FOR SELECT 
USING ("user_id" = auth.uid());

CREATE POLICY "Users can insert their own courses" 
ON "public"."courses" FOR INSERT 
WITH CHECK ("user_id" = auth.uid());

CREATE POLICY "Users can update their own courses" 
ON "public"."courses" FOR UPDATE 
USING ("user_id" = auth.uid());

CREATE POLICY "Users can delete their own courses" 
ON "public"."courses" FOR DELETE 
USING ("user_id" = auth.uid());

-- RLS Policies for chapters table
CREATE POLICY "Users can view chapters of their courses" 
ON "public"."chapters" FOR SELECT 
USING ((SELECT "user_id" FROM "public"."courses" WHERE "id" = "chapters"."course_id") = auth.uid());

CREATE POLICY "Users can insert chapters into their courses" 
ON "public"."chapters" FOR INSERT 
WITH CHECK ((SELECT "user_id" FROM "public"."courses" WHERE "id" = "course_id") = auth.uid());

CREATE POLICY "Users can update chapters of their courses" 
ON "public"."chapters" FOR UPDATE 
USING ((SELECT "user_id" FROM "public"."courses" WHERE "id" = "chapters"."course_id") = auth.uid());

CREATE POLICY "Users can delete chapters of their courses" 
ON "public"."chapters" FOR DELETE 
USING ((SELECT "user_id" FROM "public"."courses" WHERE "id" =
"chapters"."course_id") = auth.uid()); */