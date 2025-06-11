package it.auties.analyzer

import org.openqa.selenium.devtools.v137.debugger.Debugger
import org.openqa.selenium.devtools.v137.network.Network
import java.util.Optional.empty

val whatsappKeys: Keys = Keys()

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
    val driver = initialize()
    val tools = driver.devTools
    tools.createSession()
    tools.send(Debugger.enable(empty()))
    tools.send(Debugger.setBreakpointsActive(true))

    tools.addListener(Debugger.scriptParsed(), { scriptParsedEvent ->
        onWhatsappScriptLoaded(tools, scriptParsedEvent, "\"AES-GCM\",!1")
    })

    tools.addListener(Debugger.paused(), { pausedEvent ->
        onBreakpointTriggered(tools, pausedEvent)
    })

    tools.send(Network.enable(empty(), empty(), empty()))

    tools.addListener(Network.webSocketFrameSent(), { webSocketFrameSentEvent ->
        handleSentMessage(webSocketFrameSentEvent)
    })

    tools.addListener(Network.webSocketFrameReceived(), { webSocketFrameReceivedEvent ->
        handleReceivedMessage(webSocketFrameReceivedEvent)
    })

    driver["https://web.whatsapp.com/"]
}