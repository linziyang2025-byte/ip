# Geek User Guide

Geek is a desktop task manager that keeps track of todos, deadlines, and
events through text commands.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add an undated todo |
| `deadline DESCRIPTION /by DATE` | Add a deadline |
| `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | Add an event |
| `list` | Show all tasks in their current order |
| `sort` | Sort dated tasks chronologically |
| `find KEYWORD` | Find tasks by description |
| `on DATE` | Show tasks occurring on a date |
| `mark NUMBER` | Mark a task as done |
| `unmark NUMBER` | Mark a task as not done |
| `delete NUMBER` | Delete a task |
| `bye` | Exit Geek |

Dates can be entered in formats such as `2019-12-02`, `2/12/2019`, or
`Dec 2 2019`. Include a time for events and timed deadlines, for example
`2/12/2019 1800` or `Dec 2 2019 6:00 PM`.

## Sorting tasks chronologically

Enter `sort` without arguments:

```text
sort
```

Geek sorts deadlines by their due date and time, and events by their start
date and time. Dated tasks are ordered from earliest to latest. Todos have no
date, so they appear after all dated tasks. Tasks with the same date and time
keep their previous relative order.

The new order is saved automatically and remains after Geek is restarted.
