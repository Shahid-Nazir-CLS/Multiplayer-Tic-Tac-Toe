<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!DOCTYPE html>
    <html>

    <head>
        <title>Tic Tac Toe</title>
        <!-- Include Bootstrap CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/game.css">


    </head>

    <body>
        <div class="container tic-tac-toe-container">
            <%@ include file="userstats.jsp" %>
                <%@ include file="userhistorycontainer.jsp" %>
                    <div id="game-container" class="hidden">

                        <div class="header-container">
                            <span id="username">Shahid</span>
                            <span id="opponent">Zahid</span>
                        </div>
                        <div class="table-responsive">
                            <table class="table tic-tac-toe-table text-center">
                                <tr>
                                    <td><button id="cell-0-0" class="btn tic-tac-toe-button active"></button></td>
                                    <td><button id="cell-0-1" class="btn tic-tac-toe-button active"></button></td>
                                    <td><button id="cell-0-2" class="btn tic-tac-toe-button active"></button></td>
                                </tr>
                                <tr>
                                    <td><button id="cell-1-0" class="btn tic-tac-toe-button active"></button></td>
                                    <td><button id="cell-1-1" class="btn tic-tac-toe-button active"></button></td>
                                    <td><button id="cell-1-2" class="btn tic-tac-toe-button active"></button></td>
                                </tr>
                                <tr>
                                    <td><button id="cell-2-0" class="btn tic-tac-toe-button active"></button></td>
                                    <td><button id="cell-2-1" class="btn tic-tac-toe-button active"></button></td>
                                    <td><button id="cell-2-2" class="btn tic-tac-toe-button active"></button></td>
                                </tr>
                            </table>
                        </div>
                        <p class="text-center status-text">Current Player: <strong
                                id="game-current-player">${game.currentPlayer}</strong></p>
                        <p class="text-center status-text">Status: <strong id="game-status">${game.status}</strong></p>
                    </div>
        </div>

        <!-- Include Bootstrap JS and dependencies -->
        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    </body>

    </html>