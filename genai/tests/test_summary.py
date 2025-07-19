from models.summary import SummaryResponse
import os
import sys
import json

# Add the parent directory to the path to import the application modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


class TestSummaryResponse:
    """Test cases for SummaryResponse model"""
    
    def test_summary_response_creation(self):
        """Test creating a SummaryResponse instance"""
        response = SummaryResponse(
            chapter_title="Test Chapter",
            summary_markdown="# Test Summary\n\nThis is a test.",
            emoji="📚",
            source_file="test.pdf"
        )
        
        assert response.chapter_title == "Test Chapter"
        assert response.summary_markdown == "# Test Summary\n\nThis is a test."
        assert response.emoji == "📚"
        assert response.source_file == "test.pdf"
    
    def test_summary_response_dict_conversion(self):
        """Test converting SummaryResponse to dictionary"""
        response = SummaryResponse(
            chapter_title="Machine Learning",
            summary_markdown="# Machine Learning\n\n## Introduction\nML is important.",
            emoji="🤖",
            source_file="ml_lecture.pdf"
        )
        
        response_dict = response.model_dump()
        
        assert response_dict["chapter_title"] == "Machine Learning"
        assert response_dict["summary_markdown"] == "# Machine Learning\n\n## Introduction\nML is important."
        assert response_dict["emoji"] == "🤖"
        assert response_dict["source_file"] == "ml_lecture.pdf"
    
    def test_summary_response_json_serialization(self):
        """Test JSON serialization of SummaryResponse"""
        response = SummaryResponse(
            chapter_title="Data Science",
            summary_markdown="# Data Science\n\nData science overview.",
            emoji="📊",
            source_file="data_science.pdf"
        )
        
        json_str = response.model_dump_json()
        parsed = json.loads(json_str)
        
        assert parsed["chapter_title"] == "Data Science"
        assert parsed["summary_markdown"] == "# Data Science\n\nData science overview."
        assert parsed["emoji"] == "📊"
        assert parsed["source_file"] == "data_science.pdf"
    
    def test_summary_response_with_special_characters(self):
        """Test SummaryResponse with special characters in content"""
        response = SummaryResponse(
            chapter_title="Math & Physics",
            summary_markdown="# Math & Physics\n\n- Equations: E=mc²\n- Greek letters: α, β, γ",
            emoji="🧮",
            source_file="math_physics.pdf"
        )
        
        assert response.chapter_title == "Math & Physics"
        assert "E=mc²" in response.summary_markdown
        assert "α, β, γ" in response.summary_markdown
        assert response.emoji == "🧮"
    
    def test_summary_response_with_long_content(self):
        """Test SummaryResponse with long markdown content"""
        long_markdown = "# Long Chapter\n\n" + "This is a very long sentence. " * 100
        
        response = SummaryResponse(
            chapter_title="Long Chapter Title",
            summary_markdown=long_markdown,
            emoji="📖",
            source_file="long_document.pdf"
        )
        
        assert response.chapter_title == "Long Chapter Title"
        assert len(response.summary_markdown) > 1000
        assert response.emoji == "📖"
        assert response.source_file == "long_document.pdf"
    
    def test_summary_response_with_empty_markdown(self):
        """Test SummaryResponse with empty markdown content"""
        response = SummaryResponse(
            chapter_title="Empty Chapter",
            summary_markdown="",
            emoji="🔍",
            source_file="empty.pdf"
        )
        
        assert response.chapter_title == "Empty Chapter"
        assert response.summary_markdown == ""
        assert response.emoji == "🔍"
        assert response.source_file == "empty.pdf"
