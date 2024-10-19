
var stompClient = null

const setConnected = connected => (
    $('#connect').prop('disabled', connected),
    $('#disconnect').prop('disabled', !connected),
    connected
    ? $('#conversation').show()
    : $('#conversation').hide(),
    $('#greetings').html('')
)

const sendName = () =>
    stompClient.send(
        '/app/hello',
        {},
        JSON.stringify({
            name: $('#name').val()
        })
    )

const connect = (
    socket = null
) => (
    socket = new SockJS('/websocket-endpoint-url'), // same as in WebSocketConfiguration.java
    stompClient = Stomp.over(socket),
    stompClient.connect(
        {},
        frame => (
            setConnected(true),
            console.log(`Connected: ${frame}`),
            stompClient.subscribe(
                '/topic/greetings', // subscription topic published in websockets/GreetingsController.java
                greeting =>
                    showGreeting(
                        JSON.parse(
                            greeting?.body ?? {}
                        )?.content ?? ''
                    )
            )
        )
    )
)

const disconnect = () => (
    !!stompClient && stompClient.disconnect(),
    setConnected(false),
    console.log('Disconnected')
)

const showGreeting = message =>
    $('#greetings').append(
        '<tr><td>'
        + message
        + '</td></tr>'
    )

$(() => (
    $('form').on(
        'submit',
        e => e.preventDefault()
    ),
    $('#connect').click(() => connect()),
    $('#disconnect').click(() => disconnect()),
    $('#send').click(() => sendName())
))