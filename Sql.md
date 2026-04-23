-- =========================
-- CORE CONTENT STRUCTURE
-- =========================

Course (
  id PK,
  title,
  description,
  level,
  language,
  is_published,
  created_at,
  updated_at
);

Module (
  id PK,
  course_id FK,
  title,
  position,
  created_at
);

Lesson (
  id PK,
  module_id FK,
  title,
  description,
  position,
  duration,
  is_preview,
  created_at
);

-- =========================
-- CONTENT SYSTEM
-- =========================

Content (
  id PK,
  lesson_id FK,
  type,            -- VIDEO, ARTICLE, FILE, QUIZ
  position,
  created_at
);

VideoContent (
  id PK,
  content_id FK,
  url,
  duration,
  provider
);

ArticleContent (
  id PK,
  content_id FK,
  body TEXT
);

FileContent (
  id PK,
  content_id FK,
  file_url,
  file_type
);

-- =========================
-- QUIZ SYSTEM
-- =========================

Quiz (
  id PK,
  content_id FK,
  title
);

Question (
  id PK,
  quiz_id FK,
  question_text,
  type             -- multiple_choice, open
);

Answer (
  id PK,
  question_id FK,
  answer_text,
  is_correct
);

-- =========================
-- USERS & PROGRESS
-- =========================

User (
  id PK,
  name,
  email,
  password,
  created_at
);

Enrollment (
  id PK,
  user_id FK,
  course_id FK,
  enrolled_at,
  progress
);

LessonProgress (
  id PK,
  user_id FK,
  lesson_id FK,
  status,             -- NOT_STARTED, IN_PROGRESS, COMPLETED
  progress_percent,
  last_position,
  time_spent,
  completed_at,
  updated_at
);

-- =========================
-- QUIZ TRACKING
-- =========================

QuizAttempt (
  id PK,
  user_id FK,
  quiz_id FK,
  score,
  max_score,
  attempt_number,
  time_spent,
  attempted_at
);

QuestionAttempt (
  id PK,
  quiz_attempt_id FK,
  question_id FK,
  selected_answer_id FK,
  is_correct,
  time_spent
);

-- =========================
-- RECOMMENDATION SYSTEM (FUTURE)
-- =========================

Recommendation (
  id PK,
  user_id FK,
  lesson_id FK,
  score,
  reason,
  created_at
);

-- =========================
-- LEARNING STRUCTURE ENHANCEMENTS
-- =========================

LessonDependency (
  id PK,
  lesson_id FK,
  depends_on_lesson_id FK
);
