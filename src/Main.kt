import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

/*
* Используем существующие функции
* */
fun main() {
    //этап 1
/*    val clientArguments = arrayOf("127.0.0.1", "1777")
    val serverArguments = arrayOf("1777")
    main_client(clientArguments)
    main_server(serverArguments)*/

    val address = "127.0.0.1"
    val port = 1777



    //этап 2
/*    client(adr = address, portNumber = port)
    server(portNumber = port)*/

    //этап 3
    val client = Client(address, port)
    val server = Server(port)

    //Запуск
    server.start()
    client.start()
}


//этап 1 - используем существующий код
fun main_client(args: Array<String>) {
    // Определяем номер порта, на котором нас ожидает сервер для ответа
    val portNumber = 1777
    val adr = "127.0.0.1"
    println("Start!")

    val socket = Socket(adr, portNumber)
    val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    val printWriter = PrintWriter(socket.getOutputStream(), true)

    while (true) {
        //чтение клавиатуры
        val str = readLine()

        if (str != "fin") {
            // Отправляем сообщение на сервер
            printWriter.println(str)
        } else {
            break
        }
    }

    //закрываем
    bufferedReader.close()
    printWriter.close()
    socket.close()

    println("Client is finished")
}

fun main_server(args: Array<String>) {
    // Определяем номер порта, который будет "слушать" сервер
    val port = 1777

    try {
        // Открыть серверный сокет (ServerSocket)
        val servSocket = ServerSocket(port)

        // Входим в бесконечный цикл - ожидаем соединения
        while (true) {
            println("Waiting for a connection on $port")

            // Получив соединение начинаем работать с сокетом
            val fromClientSocket = servSocket.accept()

            // Работаем с потоками ввода-вывода,
            // используем блок use автоматически завершающий потоки ввода-вывода при выходе из блока.

            fromClientSocket
                .use { localSocket ->
                    PrintWriter(localSocket.getOutputStream(), true)
                        .use { printWriter ->
                            BufferedReader(InputStreamReader(localSocket.getInputStream()))
                                .use { bufferedReader ->


                                    // Читаем сообщения от клиента
                                    while (true) {
                                        val line = bufferedReader.readLine()
                                        // Печатаем сообщение
                                        println(line)
                                        // Ожидаем сообщение от клиента с содержанием "bye" для прекращения цикла обмена.
                                        if (line == "END") {
                                            // Если получено END - завершаем цикл обмена.
                                            // Отправляем клиенту сообщение окончания сеанса "END".
                                            printWriter.println("END")
                                            // Завершаем цикл
                                            break
                                        }
                                    }
                                }
                        }
                }
        }
    } catch (ex: IOException) {
        //Вывод трассировки ошибки в поток вывода консоли System.out.
        ex.printStackTrace(System.out)
    }
}


//Этап 2 - преобразуем код в функции выполняющие требуемые задачи.
//Передаём адрес и порт в параметрах функции
//Поскольку клиент теперь не читает ничего, не используем  BufferedReader
fun client(adr: String = "127.0.0.1", portNumber: Int = 1777) {

    println("Client is started!")

    val socket = Socket(adr, portNumber)
//    val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    val printWriter = PrintWriter(socket.getOutputStream(), true)


    while (true) {
        //читаем клавиатуру
        val str = readlnOrNull()
        if (str != "fin" && str != null) {
            // Отправляем сообщение на сервер
            printWriter.println(str)
        } else {
            // Завершаем сеанс
            printWriter.println("END")
            break
        }
    }


    //закрываем
//    bufferedReader.close()
    printWriter.close()
    socket.close()

    println("Client is finished")
}

//Передаём порт в параметрах функции
//Поскольку сервер теперь не передаёт ничего, не используем  PrintWriter
fun server(portNumber: Int = 1777) {
    try {
        // Открыть серверный сокет (ServerSocket)
        val servSocket = ServerSocket(portNumber)

        // Входим в бесконечный цикл - ожидаем соединения
        while (true) {
            println("Waiting for a connection on $portNumber")

            // Получив соединение начинаем работать с сокетом
            val fromClientSocket = servSocket.accept()

            // Работаем с потоками ввода-вывода,
            // используем блок use автоматически завершающий потоки ввода-вывода при выходе из блока.

            fromClientSocket
                .use { localSocket ->
//                    PrintWriter(localSocket.getOutputStream(), true)
//                        .use { printWriter ->
                    BufferedReader(InputStreamReader(localSocket.getInputStream()))
                        .use { bufferedReader ->
                            // Читаем сообщения от клиента
                            while (true) {
                                val line = bufferedReader.readLine()
                                // Печатаем сообщение
                                println(line)
                                // Ожидаем сообщение от клиента с содержанием "bye" для прекращения цикла обмена.
                                if (line == "END") {
                                    // Если получено END - завершаем цикл обмена.
                                    break
                                }
                            }
                        }
                }
        }
    } catch (ex: IOException) {
        //Вывод трассировки ошибки в поток вывода консоли System.out.
        ex.printStackTrace(System.out)
    }
}

//Этап 3.
//Чтобы иметь возможность запустить параллельно обе функции, нам нужно запустить их в отдельных потоках.
//Поскольку теперь мы не можем передать адреса и порту в виде параметров
//Мы сохраняем их, как поля экземпляров класса
class Server(val port: Int = 1777) : Thread() {

    override fun run() {
        try {
            // Открыть серверный сокет (ServerSocket)
            val servSocket = ServerSocket(port)

            // Входим в цикл - ожидаем соединения
            while (true) {
                println("Waiting for a connection on $port")
                val fromClientSocket = servSocket.accept()
                fromClientSocket.use { localSocket ->
                    BufferedReader(InputStreamReader(localSocket.getInputStream())).use { bufferedReader ->
                        // Читаем сообщения от клиента
                        while (true) {
                            val line = bufferedReader.readLine()
                            // Ожидаем сообщение от клиента с содержанием "END" для прекращения цикла обмена.
                            if (line != "END") {
                                // Если это не сообщение для завершения сеанса, печатаем сообщение
                                println("#$line")
                            } else {
                                return
                            }
                        }
                    }
                }
            }

        } catch (ex: IOException) {
            //Вывод трассировки ошибки в поток вывода консоли System.out.
            ex.printStackTrace(System.out)
        }
        println("Server terminated.")
    }
}

//Чтобы иметь возможность запустить параллельно обе функции, нам нужно запустить их в отдельных потоках.
//Поскольку теперь мы не можем передать адреса и порту в виде параметров
//Мы сохраняем их, как поля экземпляров класса
class Client(
    val adr: String = "127.0.0.1",
    val portNumber: Int = 1777
) : Thread() {
    override fun run() {
        println("Client is started!")
        val clientSocket = Socket(adr, portNumber)
        clientSocket.use { socket ->
            PrintWriter(socket.getOutputStream(), true).use { printWriter ->
                while (true) {
                    //читаем клавиатуру
                    val str = readlnOrNull()
                    if (str != "fin" && str != null) {
                        // Отправляем сообщение на сервер
                        printWriter.println(str)
                    } else {
                        // Завершаем сеанс
                        printWriter.println("END")
                        break
                    }
                }
            }
        }

        println("Client is finished")
    }
}
