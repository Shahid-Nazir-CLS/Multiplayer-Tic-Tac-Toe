<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/userstats.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/userhistorycontainer.css">
            <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;500;700&display=swap" rel="stylesheet">
            <title>Chat Application</title>
        </head>

        <body>

            <h2>Multiplayer Tic Tac Toe</h2>

            <div class="user-form" id="username-page">
                <form id="usernameForm">
                    <label for="nickname">Nickname:</label>
                    <input type="text" id="nickname" name="nickname" required>

                    <label for="nickname">Password:</label>
                    <input type="password" id="password" name="password" required>

                    <label for="fullname">Real Name:</label>
                    <input type="text" id="fullname" name="realname" required>

                    <button type="submit">Enter Game</button>
                </form>
            </div>

            <div class="chat-container hidden" id="chat-page">
                <div class="users-list">
                    <div class="users-list-container" id="users-list-container-div">
                        <h4>User : <span id="connected-user-fullname"></span></h4>
                        <p></p>
                        <hr style=" width: 100%; height: 2px; border: none; background-color: white; margin: 10px 0; ">
                        <h2>Online Users</h2>
                        <ul id="connectedUsers">
                        </ul>
                    </div>
                    <div>
                        <p><a class="logout" href="javascript:void(0)" id="match">Start Match</a></p>
                        <div class="spinner hidden" id="spinner">
                            <div class="spinner-circle"></div>
                            <p>Finding match...</p>
                        </div>
                        <p><a class="logout hidden" href="javascript:void(0)" id="cancel">Cancel Match</a></p>
                        <p><a class="logout hidden" href="javascript:void(0)" id="leave">Leave Match</a></p>
                        <p><a class="logout" href="javascript:void(0)" id="history">Show History</a></p>
                        <a class="logout" href="javascript:void(0)" id="logout">Logout</a>
                    </div>
                </div>

                <%@ include file="game.jsp" %>

                    <div class="chat-area">
                        <%@ include file="leaderboard.jsp" %>

                            <div class="chat-area" id="chat-messages" hidden>
                            </div>

                            <form id="messageForm" name="messageForm" class="hidden">
                                <div class="message-input">
                                    <input autocomplete="off" type="text" id="message"
                                        placeholder="Type your message...">
                                    <button>Send</button>
                                </div>
                            </form>
                    </div>
            </div>

            <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.4/sockjs.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
            <script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
        </body>

        </html>