# Problem Statement

## Team Information

**ReleaseRangers**

- Florian Charrot
- Jonathan Müller
- Luis Leutbecher

## Problem Statement

Many students struggle with organizing their lecture material throughout the semester. When it comes to exam preparation, the majority of the students spend a lot of time summarizing all the material to gain an overview of the lectures content. Even though the process might help with exam preparation, we are convinced that more efficient methods exist to prepare time-efficient and thoroughly for exams. This especially holds true when dealing with messy lecture notes and large volume of documents.

### What is the main functionality?

Our application helps students to study efficient by leveraging LLM generated smart summaries of their lecture material. Our vision is to create one single place where one can get a summarized overview of the lecture material needed for exam preparation. We want to enable students to easily add new content throughout the semester which constantly gets summarized to always provide the student with an up to date overview of the current course content. (Additionally, to the summarization we want to empower the student with smart studying features, leveraging our generated summaries, to support the students learning experience.)

We are aware that solutions like Notion (esp. Notion in combination with its AI feature) exist which in theory provide a similar functionality. Nevertheless to the best of our knowledge there is no solution that solely aims to tackle the problem of efficiently creating an lecture material overview for exam preperation. Our goal is to optimize our app for exactly this purpose. We especially want to emphasize the benefit of quickly adding new material on the go without selecting or prompting an LLM for a new summary.

### Who are the intended users?

We design and optimize our app to support students during their studies. However, this doesn't exclude hobbyists or part-time learners who use our platform to gain a comprehensive overview of the newly acquired content.

### How will you integrate GenAI meaningfully?

After receiving the material the user wants to prepare for, we utilize GenAI to create or classify the information into learning chapters. The creation of those learning chapters helps users avoid feeling overwhelmed by a comprehensive summary of the entire lecture content. Instead, they can focus on different learning areas of the provided course.
Next for each learning chapter GenAI creates summaries of the most important information to give an overview of the material to be learned. The returned summaries will automatically be grouped and displayed to the user in the previously created learning chapters.

(
potential additional use of GenAI:

- Knowledge retrieval
- Enhance learning experience through automated quizzes
- flashcard generation or chat with your material
)

### Describe some scenarios how your app will function?

First, after visiting our web app, the user creates a course by specifying a name. Subsequently, he is presented with a large drag-and-drop field where he can upload the initial materials of his course in the form of PDF files (with the potential for additional file types in the future). After a few seconds, the user is presented with a list of learning chapters, each containing a beautifully rendered summary of the corresponding material in markdown format.
Now the user can interact with these summaries, create additional courses and can always come back later to quickly upload additional material to a selected course which is then categorized within the respective learning chapters, leading to updates of the summaries.

In the future we might support additional features such as an export functionality of the generated Markdown document, an integration with popular Markdown editors like Notion or also the creation of study card based on the generated summaries. Of course a chatbot for asking questions about a summary is also on our backlog to further improve the experience.
