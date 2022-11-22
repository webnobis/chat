package com.webnobis.chat.client;

import com.webnobis.chat.model.Message;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Chat client<br>
 * The client is build from HTML pages, running in any Web Browser.
 *
 * @author steffen
 */
public record ChatClient(String chatPathAction, String newMessageFieldId) {

    private static final String HTML_CHAT_PAGE = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Chat client</title>
                <meta content="multipart/form-data">
                <script language="javascript">
                function scrollDown() '{'
                    var area = document.getElementById("area");
                    area.scrollTop = area.scrollHeight;
                '}'
                function setFocus() '{'
                    document.getElementById("send").focus();
                '}'
                </script>
            </head>
            <body onLoad="javascript:scrollDown();setFocus();">
            <h1>Chat client</h1>
            <form action="{0}" method="post">
                <p>Chat flow:</p>
                <textarea id="area" name="list" wrap="hard" cols="100" rows="5" readonly="true">{1}</textarea>
                <p>New message:</p>
                <input id="send" name="{2}" type="text" size="100">
                <input type="submit" value="Send">
            </form>
            </body>
            </html>""";

    private static final String HTML_SENT_PAGE = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Chat client</title>
                <meta http-equiv="refresh" content="1; url={0}">
            </head>
            <body>
            <p>{1}</p>
            </body>
            </html>""";

    /**
     * Creates the main chat page, listing each messages
     *
     * @param messages messages
     * @return chat page
     */
    public String createChatClientPage(List<Message> messages) {
        return MessageFormat.format(HTML_CHAT_PAGE, chatPathAction, toString(messages), newMessageFieldId);
    }

    private static String toString(List<Message> messages) {
        return Optional.ofNullable(messages).map(list -> list.stream().map(Message::toString).collect(Collectors.joining(System.lineSeparator()))).orElse("");
    }

    /**
     * Creates the success message sent page
     *
     * @param message message
     * @return sent page
     */
    public String createSentClientPage(String message) {
        return MessageFormat.format(HTML_SENT_PAGE, chatPathAction, message);
    }

}
