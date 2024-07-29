<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Tic Tac Toe Game</title>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
            <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;500;700&display=swap" rel="stylesheet">
            <style>
                body,
                html {
                    height: 100%;
                    margin: 0;
                    font-family: 'Roboto', sans-serif;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    background: linear-gradient(135deg, #232526, #414345);
                    color: rgb(150, 150, 150);
                    text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.3);
                }

                .container {
                    text-align: center;
                }

                .title {
                    font-size: 6rem;
                    font-weight: 700;
                    margin-bottom: 2rem;
                    animation: fadeIn 1.5s ease-in-out;
                }

                .start-btn {
                    padding: 1rem 3rem;
                    font-size: 1.2rem;
                    font-weight: 500;
                    border-radius: 50px;
                    border: none;
                    background: #0f2027;
                    background: linear-gradient(135deg, #0f2027, #203a43, #2c5364);
                    color: #fff;
                    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
                    transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
                }

                .start-btn:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
                }

                .start-btn:focus {
                    outline: none;
                }

                @keyframes fadeIn {
                    from {
                        opacity: 0;
                    }

                    to {
                        opacity: 1;
                    }
                }
            </style>
        </head>

        <body>
            <div class="container">
                <div class="title">
                    Tic Tac Toe
                </div>
                <button class="start-btn" onclick="startGame()">Start Game</button>
            </div>

            <script>
                function startGame() {
                    window.location = "/game";
                }
            </script>
        </body>

        </html>