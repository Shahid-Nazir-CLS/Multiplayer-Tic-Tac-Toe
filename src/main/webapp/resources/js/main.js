'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const match = document.querySelector('#match');
const gameContainer = document.querySelector('#game-container');
const username = document.querySelector('#username');
const opponenetName = document.querySelector('#opponent');
const currentPlayer = document.querySelector('#game-current-player');
const gameStatus = document.querySelector('#game-status');
const cancel = document.querySelector('#cancel');
const history = document.querySelector('#history');

let stompClient = null;
let nickname = null;
let fullname = null;
let password = null;
let selectedUserId = null;
let currGame = null;

async function connect(event) {
  nickname = document.querySelector('#nickname').value.trim();
  fullname = document.querySelector('#fullname').value.trim();
  password = document.querySelector('#password').value.trim();

  if (nickname && fullname && password) {
    const user = {
      nickName: nickname,
      hashedPassword: password, // Ensure this is a hashed string
      fullName: fullname,
      status: 'ONLINE',
    };

    fetch('/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(user),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log('Success:', data);
        // after user is authenticated, create a websocket connection
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
      })
      .catch((error) => {
        alert('username already taken or wrong password.');
        console.error('Error:', error);
      });
  }
  event.preventDefault();
}

function onConnected() {
  usernamePage.classList.add('hidden');
  chatPage.classList.remove('hidden');

  stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
  stompClient.subscribe(`/user/${nickname}/queue/game`, updateGameBoard);
  stompClient.subscribe(`/topic/public`, onMessageReceived);

  stompClient.subscribe(`/user/${nickname}/queue/game`, (message) => {
    handleGameStart(JSON.parse(message.body));
  });

  // register the connected user
  stompClient.send(
    '/app/user.addUser',
    {},
    JSON.stringify({
      nickName: nickname,
      hashedPassword: password,
      fullName: fullname,
      status: 'ONLINE',
    })
  );

  document.querySelector('#connected-user-fullname').textContent = fullname;

  showLeaderBoard();
}

async function showLeaderBoard() {
  try {
    const leaderboardRes = await fetch(`/user/leaderboard`);
    const leaderboard = await leaderboardRes.json();
    console.log('Retrieved leaderboard');
    console.log(leaderboard);

    const leaderList = document.getElementById('leaderList');
    leaderList.innerHTML = ''; // Clear the list before appending new items

    leaderboard.forEach((user, index) => {
      appendLeaderElement(user, leaderList, index + 1);
    });
  } catch (error) {
    console.error('Error fetching leaderboard:', error);
  }
}

function appendLeaderElement(user, leaderList, rank) {
  // Create a button element instead of an li element
  const listItem = document.createElement('button');
  listItem.classList.add('leader-item');

  // Create username element
  const usernameDiv = document.createElement('div');
  usernameDiv.classList.add('leader-stats');
  usernameDiv.textContent = `Rank : ${rank}  |  Username: ${user.nickname}`;

  // Create stats element
  const statsDiv = document.createElement('div');
  statsDiv.classList.add('leader-stats');
  statsDiv.textContent = `W: ${user.wins} | L: ${user.losses} | D: ${user.draws}`;

  // Append elements to listItem
  listItem.appendChild(usernameDiv);
  listItem.appendChild(statsDiv);

  // Add click event listener to listItem
  listItem.addEventListener('click', async () => {
    console.log(`Clicked on user with nickname: ${user.nickname}`);
    selectedUserId = user.nickname;
    await fetchAndDisplayUserStats();
  });

  // Add the button to the leader list
  leaderList.appendChild(listItem);
}

async function handleGameStart(game) {
  console.log('Game started. Your game room message is :  ' + game);
  currGame = game;

  // hide start match and show leave match and hide spinner and hide online users
  document.querySelector('#leave').classList.remove('hidden');
  document.querySelector('#spinner').classList.add('hidden');
  document.querySelector('#cancel').classList.add('hidden');
  document.getElementById('user-stats-container').hidden = true;
  document.getElementById('users-list-container-div').hidden = true;
  document.getElementById('users-history-container-div').hidden = true;
  document.getElementById('leader-container-div').hidden = true;
  history.hidden = true;

  // show chat area
  chatArea.hidden = false;
  messageForm.hidden = false;

  // Update username and opponent name
  if (game.player1Id === nickname) {
    username.innerHTML = nickname + '(' + game.player1Symbol + ')';
    opponenetName.innerHTML = game.player2Id + '(' + game.player2Symbol + ')'; // The opponent’s name
  } else {
    username.innerHTML = nickname + '(' + game.player2Symbol + ')';
    opponenetName.innerHTML = game.player1Id + '(' + game.player1Symbol + ')';
  }

  // Assign the opposite of the current user's nickname to selectedUserId
  selectedUserId =
    game.player1Id === nickname ? game.player2Id : game.player1Id;

  await updateGameBoard(game);
  await fetchAndDisplayUserChat(currGame.gameId);
}

async function updateGameBoard(game) {
  if (!game.board) {
    return;
  }
  console.log('updating game board');
  currentPlayer.innerHTML = game.currentPlayer;
  gameStatus.innerHTML = game.status;

  // Check if it is the current player's turn
  const isCurrentPlayerTurn = game.currentPlayer === nickname;

  // Determine if the game is won
  const isGameWon = game.status.includes('WINS');

  // Loop through the board array received from the server
  for (let row = 0; row < 3; row++) {
    for (let col = 0; col < 3; col++) {
      let cellId = `cell-${row}-${col}`;
      let cell = document.getElementById(cellId);
      if (cell) {
        // Update cell content
        if (game.board[row][col] !== '-') {
          cell.innerHTML = game.board[row][col];
          cell.disabled = true;
          cell.classList.add('disabled');
          cell.classList.remove('active');
        } else {
          cell.innerHTML = '';

          // Enable the cell if it's the user's turn and the game isn't won
          if (isCurrentPlayerTurn && !isGameWon) {
            cell.disabled = false;
            cell.classList.add('active');
            cell.classList.remove('disabled');
          } else {
            cell.disabled = true;
            cell.classList.add('disabled');
            cell.classList.remove('active');
          }
        }

        // Disable all cells if the game is won
        if (isGameWon) {
          cell.disabled = true;
          cell.classList.add('disabled');
          cell.classList.remove('active');
        }
      }
    }
  }
}

async function fetchAndDisplayUserChat(gameId) {
  try {
    console.log('Fetching messages of user: ' + selectedUserId);
    const userChatResponse = await fetch(`/messages/${gameId}`);
    if (!userChatResponse.ok) {
      throw new Error(
        'Network response was not ok ' + userChatResponse.statusText
      );
    }
    const userChat = await userChatResponse.json();
    console.log('Messages received:', userChat);

    chatArea.innerHTML = ''; // Clear the chat area
    userChat.forEach((chat) => {
      displayMessage(chat.senderId, chat.content);
    });
    messageForm.classList.remove('hidden');
    gameContainer.classList.remove('hidden');

    chatArea.scrollTop = chatArea.scrollHeight; // Scroll to the bottom of the chat area
  } catch (error) {
    console.error('Failed to fetch user chat:', error);
  }
}

async function onMessageReceived(payload) {
  console.log('received new message');
  await findAndDisplayConnectedUsers();
  console.log('Message received', payload);
  const message = JSON.parse(payload.body);
  if (selectedUserId && selectedUserId === message.senderId) {
    displayMessage(message.senderId, message.content);
    chatArea.scrollTop = chatArea.scrollHeight;
  }

  if (selectedUserId != null) {
    document.querySelector(`#${selectedUserId}`).classList.add('active');
  } else {
    messageForm.classList.add('hidden');
  }

  const notifiedUser = document.querySelector(`#${message.senderId}`);
  if (notifiedUser && !notifiedUser.classList.contains('active')) {
    const nbrMsg = notifiedUser.querySelector('.nbr-msg');
    nbrMsg.classList.remove('hidden');
    nbrMsg.textContent = '';
  }
}

async function findAndDisplayConnectedUsers() {
  const connectedUsersResponse = await fetch('/user/connected');
  let connectedUsers = await connectedUsersResponse.json();
  // connectedUsers = connectedUsers.filter((user) => user.nickName !== nickname);
  const connectedUsersList = document.getElementById('connectedUsers');
  connectedUsersList.innerHTML = '';

  connectedUsers.forEach((user) => {
    appendUserElement(user, connectedUsersList);
    if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
      const separator = document.createElement('li');
      separator.classList.add('separator');
      connectedUsersList.appendChild(separator);
    }
  });
}

function appendUserElement(user, connectedUsersList) {
  const listItem = document.createElement('li');
  listItem.classList.add('user-item');
  listItem.id = user.nickName;

  const userImage = document.createElement('img');
  userImage.src =
    'https://static.vecteezy.com/system/resources/previews/019/879/186/non_2x/user-icon-on-transparent-background-free-png.png';
  userImage.alt = user.fullName;

  const usernameSpan = document.createElement('span');
  usernameSpan.textContent = user.fullName;

  const receivedMsgs = document.createElement('span');
  receivedMsgs.textContent = '0';
  receivedMsgs.classList.add('nbr-msg', 'hidden');

  listItem.appendChild(userImage);
  listItem.appendChild(usernameSpan);
  listItem.appendChild(receivedMsgs);

  listItem.addEventListener('click', userItemClick);

  connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {
  document.querySelectorAll('.user-item').forEach((item) => {
    item.classList.remove('active');
  });
  chatArea.hidden = true;
  messageForm.hidden = true;
  gameContainer.classList.add('hidden');

  const clickedUser = event.currentTarget;
  clickedUser.classList.add('active');

  selectedUserId = clickedUser.getAttribute('id');
  fetchAndDisplayUserStats().then;

  const nbrMsg = clickedUser.querySelector('.nbr-msg');
  nbrMsg.classList.add('hidden');
  nbrMsg.textContent = '0';
}

async function fetchAndDisplayUserStats() {
  try {
    console.log('Fetching stats  of user: ' + selectedUserId);
    const userStatsRes = await fetch(`/user/${selectedUserId}/stats`);

    const userStats = await userStatsRes.json();
    console.log(userStats);

    // Populate the user stats in the UI
    document.getElementById('nickname-stats').textContent = userStats.nickname;
    document.getElementById('fullname-stats').textContent = userStats.fullName;
    document.getElementById('games-played').textContent = userStats.gamesPlayed;
    document.getElementById('wins').textContent = userStats.wins;
    document.getElementById('losses').textContent = userStats.losses;
    document.getElementById('draws').textContent = userStats.draws;
    document.getElementById('stats-status').textContent = userStats.status;
    // convert epoch time to human readable time
    const joindate = new Date(userStats.joinedOn);
    const formattedJoinedOn = joindate.toLocaleString();
    document.getElementById('joined-on').textContent = formattedJoinedOn;
    // convert epoch time to human readable time
    const lastdate = new Date(userStats.lastOnline);
    const formattedLastOnlineDate = lastdate.toLocaleString();
    document.getElementById('last-online').textContent =
      formattedLastOnlineDate;

    // un-hide user stats div
    document.getElementById('user-stats-container').hidden = false;

    // hide history
    document.getElementById('users-history-container-div').hidden = true;
  } catch (error) {
    console.error('Failed to fetch user chat:', error);
  }
}

function displayMessage(senderId, content) {
  const messageContainer = document.createElement('div');
  messageContainer.classList.add('message');
  if (senderId === nickname) {
    messageContainer.classList.add('sender');
  } else {
    messageContainer.classList.add('receiver');
  }
  const message = document.createElement('p');
  message.textContent = content;
  messageContainer.appendChild(message);
  chatArea.appendChild(messageContainer);
}

function onError() {
  connectingElement.textContent =
    'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}

function sendMessage(event) {
  const messageContent = messageInput.value.trim();
  if (messageContent && stompClient) {
    const chatMessage = {
      senderId: nickname,
      recipientId: selectedUserId,
      gameId: currGame.gameId,
      content: messageInput.value.trim(),
      timestamp: new Date(),
    };
    stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
    displayMessage(nickname, messageInput.value.trim());
    messageInput.value = '';
  }
  chatArea.scrollTop = chatArea.scrollHeight;
  event.preventDefault();
}

async function onHistory() {
  //hide user stats and game board
  gameContainer.classList.add('hidden');
  document.getElementById('user-stats-container').hidden = true;

  // show history container
  document.getElementById('users-history-container-div').hidden = false;
  console.log('made history false');

  // show leaderboard
  document.getElementById('leader-container-div').hidden = false;

  // hide form area and chat area
  chatArea.hidden = true;
  messageForm.hidden = true;

  const gameListRes = await fetch(`/game/history/${nickname}`);
  const gameList = await gameListRes.json();
  console.log('retreived user history');
  console.log(gameList);

  const gamesList = document.getElementById('gamesList');
  gamesList.innerHTML = '';

  gameList.forEach((game) => {
    appendGameElement(game, gamesList);
    if (gameList.indexOf(game) < gameList.length - 1) {
      const separator = document.createElement('li');
      separator.classList.add('separator');
      gamesList.appendChild(separator);
    }
  });
}

function appendGameElement(game, gamesList) {
  // Create a button element instead of an li element
  const listItem = document.createElement('button');
  listItem.classList.add('game-item');

  // Determine opponent name
  const opponentName =
    game.player1Id === nickname ? game.player2Id : game.player1Id;

  // Create opponent name element
  const opponentDiv = document.createElement('div');
  opponentDiv.classList.add('opponent-name');
  opponentDiv.textContent = `Opponent : ${opponentName}`;

  // Create status element
  const statusDiv = document.createElement('div');
  statusDiv.classList.add('game-status');
  statusDiv.textContent = `Status : ${game.status}`;

  // Create game start time element
  // Convert startTime from epoch to readable date
  const date = new Date(game.startTime);
  const formattedDate = date.toLocaleString();
  const playTimeDiv = document.createElement('div');
  playTimeDiv.classList.add('play-time');
  playTimeDiv.textContent = `Played at : ${formattedDate}`;

  // Append elements to listItem
  listItem.appendChild(opponentDiv);
  listItem.appendChild(playTimeDiv);
  listItem.appendChild(statusDiv);

  // Add click event listener to listItem
  listItem.addEventListener('click', async () => {
    // Action to perform on click
    console.log(`Clicked on game with ID: ${game.gameId}`);
    await fetchAndDisplayUserChat(game.gameId);
    // show chat area
    chatArea.hidden = false;
    messageForm.hidden = true;

    // hide game container
    gameContainer.classList.add('hidden');

    // hide leaderboard
    document.getElementById('leader-container-div').hidden = true;
  });

  // Add the button to the games list
  gamesList.appendChild(listItem);
}

function onLogout() {
  stompClient.send(
    '/app/user.disconnectUser',
    {},
    JSON.stringify({
      nickName: nickname,
      fullName: fullname,
      status: 'OFFLINE',
    })
  );

  // send message to server that person leftGame
  if (currGame != null) {
    stompClient.send(
      '/app/user.leaveGame',
      {},
      JSON.stringify({ gameId: currGame.gameId, userId: nickname })
    );
  }

  window.location.reload();
}

function onLeave() {
  // hide game container
  gameContainer.classList.add('hidden');
  // hide chat container
  chatArea.hidden = true;
  messageForm.hidden = true;
  // hide all buttons except start game
  document.querySelector('#leave').classList.add('hidden');
  document.querySelector('#match').classList.remove('hidden');
  history.hidden = false;

  // show users online container
  document.getElementById('users-list-container-div').hidden = false;

  // show leaderboard
  document.getElementById('leader-container-div').hidden = false;

  // send message to server about leaving game
  if (currGame != null) {
    stompClient.send(
      '/app/user.leaveGame',
      {},
      JSON.stringify({ gameId: currGame.gameId, userId: nickname })
    );
  }
}

function onMatch(event) {
  stompClient.send(
    '/app/user.matchUser',
    {},
    JSON.stringify({ nickName: nickname, fullName: fullname, status: 'ONLINE' })
  );
  console.log('started match find');
  // show spinner and show cancel match
  document.querySelector('#spinner').classList.remove('hidden');
  document.querySelector('#cancel').classList.remove('hidden');
  document.querySelector('#match').classList.add('hidden');
  event.preventDefault();
}

document.querySelectorAll('.tic-tac-toe-button').forEach((button) => {
  button.addEventListener('click', function () {
    if (!this.disabled) {
      const [row, col] = this.id.split('-').slice(1).map(Number);
      // Send move to server (you can use fetch, WebSocket, etc.)
      sendMoveToServer(row, col);
    }
  });
});

function sendMoveToServer(row, col) {
  const message = {
    gameId: currGame.gameId,
    row: row,
    col: col,
  };
  stompClient.send('/app/game.makeMove', {}, JSON.stringify(message));
  console.log('clicked button ' + row + ' : ' + col);
}

async function onCancel() {
  const cancelStatus = await fetch(`/game/cancel/${nickname}`);
  console.log('match find cancel status : ' + cancelStatus);
  document.querySelector('#spinner').classList.add('hidden');
  document.querySelector('#cancel').classList.add('hidden');
  document.querySelector('#match').classList.remove('hidden');
}

usernameForm.addEventListener('submit', connect, true); // step 1
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
match.addEventListener('click', onMatch, true);
cancel.addEventListener('click', onCancel, true);
leave.addEventListener('click', onLeave, true);
history.addEventListener('click', onHistory, true);
window.onbeforeunload = () => onLogout();
