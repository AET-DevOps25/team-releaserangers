# Problem Statement

## Team Information

**ReleaseRangers**

- Florian Charrot
- Jonathan Müller
- Luis Leutbecher

## Problem Statement

Many students struggle with organizing their lecture material throughout the semester. When it comes to exam preparation, the majority of the students spend a lot of time summarizing all the material to gain an overview of the lecture contents. This especially holds true when dealing with messy lecture notes and large volume of documents. Even though the process might help with exam preparation, we are convinced that more efficient methods exist to prepare time-efficiently and thoroughly for exams. 

### What is the main functionality?

Our application helps students to study efficiently by leveraging LLM-generated smart-summaries of their different lecture materials. Our vision is to provide an environment where students can create one single place where they can get a summarized overview of the most relevant lecture contents needed for exam preparation. We want to enable students to easily add new content throughout the semester, which, after uploading, gets summarized automatically in order to always provide the student with an up to date overview of the covered course contents. (In addition to the summarization functionality, we want to support the students' learning experience with smart-studying features, such as chatting with the material or generating flashcards from our summaries.)

We are aware that solutions like Notion (esp. Notion in combination with its AI feature) exist and in theory provide a somewhat similar functionality. Nevertheless, to the best of our knowledge, there is no solution that solely aims to tackle the problem of efficiently creating a lecture material overview for exam preparation. Our application is optimized for exactly this purpose. We especially emphasize on the benefit of quickly being able to add new material (in whatever form it may be), without selecting or prompting an LLM explicitly.

### Who are the intended users?

We design and optimize our app to support students during their studies. However, it is also suitable for hobbyists or part-time learners who use our platform to gain a comprehensive overview of any newly acquired content they might want to keep track of.

### How will you integrate GenAI meaningfully?

After the user has uploaded the material he/she wants to prepare for, the application will first utilize GenAI to classify the information into learning chapters and fill these chapters with summarized content from the material. Structuring the material into these learning chapters helps users avoid feeling overwhelmed by providing a comprehensive and well-structured summary of the entire lecture content. This allows them to understand the extent of topics that lie ahead of them, and helps identify learning areas they might still lack knowledge in.
Next, GenAI fills these learning chapters with content by creating summaries of the most important information in a clean and study-friendly way. The returned summaries will automatically be grouped and displayed to the user in the previously created learning chapters.


Potential additional use of GenAI, if time allows:

- Knowledge retrieval (RAG)
- Enhance learning experience through automated quizzes
- Chat with your material
- Flashcard generation chat with your material


### Describe some scenarios how your app will function?

#### Scenario 1: Uploading and Summarizing Lecture Notes
Anna, a computer science student, is very stressed before her exams. She has littly time to study, so she uploads her handwritten lecture notes (PDFs annotated on her iPad) and several PPTX slides from her "Introduction to Software Engineering" course. The app automatically processes the files, uses an LLM to detect relevant topics, and organizes them into thematic learning chapters (e.g. "Requirements Elicitation", "System Desing", and "Testing"). Within seconds, Anna sees summarized content for each chapter in a clean interface, giving her a first overview over the entire course.

#### Scenario 2: Continous Semester Use
Max is an eager student and uploads his lecture material every week from the start of the semester. The app integrates the contents of each new upload into existing chapters or creates new ones as needed. This helps Max to maintain an up-to-date structured summary of the most relevant content of his course, allowing him to enter the exam phase with no fear.

#### Scenario 3: Catching Up After Missed Lecture
Tom lost his jacket in a Wiesn tent, and caught a cold walking back home. Unable to leave his bed due to the sickness, he misses two weeks of lectures. He asks a fellow student to share the notes takes during class, and uploads them into the app. The AI-generated summaries help him quickly up on missed content. He doesn't need to go through every document in detail to know what was covered.

(
#### Scenario 4: Pre-Exam Preparation with Flashcards and Chat
One week before the exams, Lisa opens the app and reviews the summarized chapters. She needs more information on a certain topic and decides to use the chatbot to chat with the material. The chatbot is based on a RAG approach, and retrieves all relevant information to that question to help Lisa deepen her understanding. She then studies by activating the “flashcard” feature, which automatically generates question-answer pairs from her summaries. 

)

#### General Scneario: Workflow through our App

Upon first visiting our web app, the user creates a course by specifying a course name. Subsequently, he/she is presented with a large drag-and-drop field where he/she can upload the initial materials of his/her course in the form of PDF or PPTX files (with the potential for additional file types in the future). After a few seconds the user is presented with a proposed list of learning chapters, each containing a beautifully rendered summary of the corresponding material in markdown format.
Now the user can interact with these summaries, create additional courses and can always come back later to quickly upload additional material to a selected course which is then categorized within the already existing learning chapters, leading to updates of the summaries.

In the future, we might support additional features such as an export functionality of the generated markdown document, an integration with popular markdown editors like Notion or also the creation of study card based on the generated summaries. Finally, a chatbot for asking questions and interacting with the summaries is also on our backlog to further improve the experience.
