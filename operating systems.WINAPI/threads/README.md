# Task

#### Write a program for a console process that consists of two threads: main and worker

The main thread must perform the following actions:
- Create an array, the dimension and elements of which are entered from the console (or generated randomly).
- Enter the time to stop and start the worker thread.
- Create a worker stream, transfer data to the stream: array size, array, etc.
- Suspend the worker thread (SuspendThread), then after a while start the thread again.
- Be able to create a stream with the _beginthreadex and CreateThread functions
- Wait for the worker thread to finish.
- Output the result of the worker thread to the console
- Complete the work.




The worker thread has to do the following work:

- Output the numbers from the array. The element type is char. After each iteration of the cycle, "sleep" for 10 milliseconds. Complete your work.