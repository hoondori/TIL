# Ch2. Concurrency on the JVM and the Java Memory Model

## Processes and Threads
* Most OS rely on pre-emptive multitasking, in which each program is repetitively assigned slices of execution time at a specific processor
* A process is an instance of a computer program that is being executed
* OS reserves a part of the memory and other computational resources and associates them with a specific computer program
* The memory and other computational resources of one process are isolated from the other processes, two processes cannot read each other's memory directly
* Each thread describes the current state of the program stack and the program counter during program execution
* Unlike seperate processes, separate OS threads within the sam process share a region of memory, and communicate by writing to and reading parts of that memory.
* Within JVM process, multiple threads ca run simultaneously
* Each java thread is directly mapped to an OS thread

### Creating and starting threads
* main thread
* In SBT, set fork to be true ( the program runs inside a separate JVM process )
* create thread and then call start.
* join : halt execution of the main thread until thread completes its execution
 * put the main thread into the waiting state until thread terminates
* OS is notified that thread terminated and eventually lets the main thread continue the execution
* most multithreaded progams are nondeterministic, and this is what makes multithreaded programming so hard

### Atomic execution
* join method has a property that all the writes to memory performed by the thread being joined occur before the join call returns
* this pattern only allows ver restricted one-way communication, and it does not allow threads to mutually communicate during their execution
* Race condition is a phenomemon in which the output of a concurrent program depends on the execution schedule of the statements in the program
* A race condition is not necessarily an incorrect program behavior. However, if some execution schedule causes an undesired program output, the race condition is considereded to be program error
* Atomic execution means that the individual statements in the block of code executed by one thread cannot interleave with those statements executed by another thread.
* **synchronized**
* every object has a intrinsic lock or a monitor which is used to ensure that only one thread is executing some synchronized block on that object
* Gain ownership of the monitor, or acuqires it... release the monitor

### Reordering
* Why can't we reason about execution of the program the way we did?
* By JMM Spec, the JVM is allowed to reorder certain program statements executed by one thread as long as it does not change the serial semantics of the program for that particular thread
* it is not true that the writes by one thread are immediately visible to all other threads
* by synchronized, all the writes to the memory are visible to all the other threads

## Monitors and synchronization
* locks are used to ensure that no two threads execute the same code simultaneously, mutual exclusion
* each object in JVM has a built-in monitor lock, called intrinsic lock
* synchronized statements can be nested

### Deadlocks
* a deadlock is two or more executions wait for each other to complete an action before processing with thir own action
* whenever resources are acquired in the same order, there is no danger of a deadlock
* a nice thing about deadlock is that a deadlock system does not progress.. easily identified
* This is unlike the errors due to race conditions, which only become apparent long after the system transitions into an invalid state

### Guarded blocks
* creating a new thread is much more expensive
* reusable threads is called a thread pool
* busy-wating thread is even more expensive than creating a new thread
* wait and notify
* it can cause spurious wakeups : JVM sometimes wake up a threads even though there is no corresponding notify call
* To guard against spurious wakeups, use wait with while loop that checks the condition

### Interrupting threads and the graceful shutdown
* If we have a lot of dormant workers lying around, we might run out of memory
* one way to stop a dormant thread from executing is to interrupt => interruptedException
* alternative is to implement graceful shutdown
* releases all its resources and terminates willingly


## Volatile variables
* atomically read and modified, and are mostly used as status flags, ex. to signal that a computation is completed or cancelled
* writes to and read from volatile variables cannot be reordered in a single thread
* writing to a volatile variable is immediately visible to all the other thread
* volatile semantics are subtle and easy to get wrong
* multiple volatile reads and writes are not atomic

## The Java Memory Model
* a language memory model is a specification that describes the circumstances under which a write to a variable become visible to other threads
* sequential consistency
* Compiler are allowed to use registers to postpone or avoid memory writes, and reorder statements to achive optimal performance, as long as it does not change the serial semantics.
* A memory model is a trade-off between the predictable behavior of a concurrent program and a compiler's ability to perform optimizations
* happends-before relationships
 * program oder
 * monitor locking
 * volatile fields
 * thread start
 * thread termination
 * transitivity

### Immutable objects and final fields
* if an object contains only final fields and the reference to the enclosing object does not become visible to another thread become visible to another thread before the constructor completes, then object is considered immutable and can be shared between the threads without any synchronization
* In scala, declaring an object field as final means that the getter for that field cannot be overridden in a subclass.
* final fields can be shared without synchronization
* The scala compiler ensures that lambda values contain only final, properly initialized fields
* however, certain collections that are deemed immutable, such as List, vector cannot be shared without synchronization














## A
* a
* 




















