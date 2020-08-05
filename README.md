# CancellingExecutor
Playing with overriding newTask of Callable to make socket server able to shutdown.
To stop waiting for the accept method the control flow runs socket closing. 
Same for opened socket, there could add some special treatment for opened sockets.

The newTask is overridden in ServerSocketUsingTask, SocketUsingTask to treat socket closing.