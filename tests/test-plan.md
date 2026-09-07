# Geek Manual Test Plan

## C-Sort

Start Geek with an empty data file, then enter the commands below in order.

1. Add tasks in a deliberately unsorted order:

   ```text
   todo read book
   deadline submit report /by 3/12/2019
   event morning meeting /from 2/12/2019 0900 /to 2/12/2019 1000
   deadline return book /by 2/12/2019
   todo write notes
   ```

2. Enter `sort`.

   Expected: `return book`, `morning meeting`, and `submit report` are shown
   in chronological order, followed by `read book` and `write notes` in their
   original relative order.

3. Exit Geek, restart it, and enter `list`.

   Expected: the order produced in step 2 is preserved.

4. Enter `sort date`.

   Expected: Geek reports that the `sort` command does not accept arguments,
   and the task list is unchanged.
