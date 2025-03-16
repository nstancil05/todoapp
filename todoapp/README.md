
# To-Do List Manager

## Overview
A **JavaFX-based To-Do List Manager** that allows users to:
- Add, edit, and delete tasks.
- View task details.
- Mark tasks as completed by clicking on them.
- Sort tasks by **due date**, **priority**, **priority & due date**, or **completion status**.
- Reverse sorting order.
- Persist tasks using `tasks.json` file for data storage.

---

## ✅ Requirements
- **Java 17 or later**
- **Maven** (Project is Maven-managed)
- **JavaFX SDK** (if running manually outside packaged JAR)

---

## 🚀 How to Run

### 1. Ensure you have the full project source code:
Make sure you have **all the source files**, including:
- `pom.xml` (Maven configuration)
- `src/` directory containing all Java classes (like `Main.java`, `Task.java`, `StorageManager.java`, etc.)

---

### 2. Open in IDE or terminal:
- Open the project in **IntelliJ IDEA**, **Eclipse**, **VS Code**, or any IDE that supports Maven.
- **OR** navigate to the project root directory via terminal/command line.

---

### 3. Compile the project:
```bash
mvn clean compile
```

---

### 4. Run the application using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.todoapp.Main"
```

---

## 💾 Data Storage
- All tasks are saved in a `tasks.json` file located in the **current working directory** where the app is launched.
- Tasks persist between application runs — no manual save is needed!

---

## ✅ Test Cases for To-Do List Manager

### Test Case 1: Add New Task
**Steps:**
1. Open the application.
2. Enter `Task Name`: "Math Homework".
3. Enter `Description`: "Finish chapter 5 problems".
4. Select `Due Date`: Tomorrow's date.
5. Select `Category`: "School".
6. Select `Priority`: "High".
7. Click **"Add Task"**.

**Expected Result:**
- The task "Math Homework" appears in the task list with correct details.
- Task is saved to `tasks.json` and persists on app restart.

---

### Test Case 2: Edit Existing Task
**Steps:**
1. Select "Math Homework" from the task list.
2. Click **"Edit Task"**.
3. Change `Description` to: "Finish chapters 5 and 6".
4. Click **"Save Changes"**.

**Expected Result:**
- Task details are updated and shown in the list.
- New description is saved and persists in `tasks.json`.

---

### Test Case 3: Mark Task as Completed
**Steps:**
1. Click on "Math Homework" in the task list.

**Expected Result:**
- Task status toggles to **completed** (indicated visually).
- Completion status is saved and persists on restart.

---

### Test Case 4: Delete Task
**Steps:**
1. Select "Math Homework".
2. Click **"Delete Task"**.

**Expected Result:**
- Task is removed from the list and `tasks.json`.
- Task does not appear when reopening the app.

---

### Test Case 5: Sort by Priority
**Steps:**
1. Add multiple tasks with varying priorities (High, Medium, Low).
2. Select **"Priority"** from the sort dropdown.
3. Click **"Sort Tasks"**.

**Expected Result:**
- Tasks sorted from High to Low priority (or reversed if **Reverse Order** is checked).

---

### Test Case 6: Sort by Due Date (Reverse)
**Steps:**
1. Select **"Due Date"** from the sort dropdown.
2. Check **"Reverse Order"** checkbox.
3. Click **"Sort Tasks"**.

**Expected Result:**
- Tasks sorted from furthest due date to nearest due date.

---

## ✅ Notes:
- The **edit mode** will show **"Save Changes"** instead of "Add Task" when editing an existing task.
- Clicking a task **toggles** its completion status.
- Sorting supports **reversing order** dynamically.
