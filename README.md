# Maze client

My take on the maze client exercise.

The client application fulfills the requirement of providing a player that can communicate with the server and play the game.

## Project structure and development choices:
The application is divided into two main sections, the communication and the players' logic.
### Communication:
The communication with the game guides and orchestrates the client application. The implementation itself is abstracted from the rest of the components trough this interface [`Communicator.java`](src/main/java/hemmouda/maze/communication/Communicator.java).

Two classes implement this interface, which are:
- [**ServerCommunicator.java**](src/main/java/hemmouda/maze/communication/servercommunicator/ServerCommunicator.java): This class communicates with the provided server trough XML messages over TCP/IP, following the provided schema.
- [**LocalCommunicator.java**](src/main/java/hemmouda/maze/communication/localcommunicator/LocalCommunicator.java): This class (which ended up not being implemented) was intended to be used while training an AI model to play the game. It would simulate the board/game locally within the application itself and provide the same interface as the server communicator, but it would be faster and more flexible for training purposes.
### The players' logic:
Since the used Communicator orchestrate the application, the players are static and more or less stateless, and only respond to whatever the Communicator queries them for.
The logic and actual implementation of the player is also abstracted away from the rest of the application trough an abstract basic class called [`Player.java`](src/main/java/hemmouda/maze/game/logic/player/Player.java). This allows for the Communicator to use a unified interface to query whatever player is actually used. And it also eases the development of new kinds of players. Three kinds of players were implemented:
- [**RandomPlayer.java**](src/main/java/hemmouda/maze/game/logic/player/randomplayer/RandomPlayer.java): This player simply chooses a random yet valid move to play. It was the first to be implemented to test and get a feel for the game. Funnily enough, it does eventually win the game after around about 2000 moves.
- [**InputPlayer.java**](src/main/java/hemmouda/maze/game/logic/player/inputplayer/InputPlayer.java): This player asks the user for input on what move to play. Honestly it was implemented just to have some fun playing the game, but it's a bit tedious to play the game this way. This player along side the RandomPlayer aren't meant to be the main players that would play the game efficiently and "reasonably".
- [**LookAheadPlayer.java**](src/main/java/hemmouda/maze/game/logic/player/lookaheadplayer/LookAheadPlayer.java): This player simply looks ahead into all the possible moves and chooses the one that leads to the treasure. A couple of optimizations were made to make it operational, which you can read more about in the class. This player plays perfectly alone, and is **pretty solid** when playing with other players.
- **Honorable mentions**:
    - Before LookAheadPlayer was conceived, a Player based on the MinMax algorithm was meant to be implemented to be an actual Player (not like the RandomPlayer or InputPlayer). However, after fiddling around with an implementation of the MinMax algorithm, I came to the realization that it cannot be used for games with more than two players. Even if we modify it to consider all other players as the second player. And here's a really good [explanation](https://stackoverflow.com/questions/14826451/extending-minimax-algorithm-for-multiple-opponents) as to why. While reading that explanation, I came across the MaxN algorithm, which seemed to be a good alternative for the MinMax algorithm, as that it's a generalization of the MinMax algorithm, and it can be used for games with an arbitrary number of players. And so, I implemented an abstract interface that would allows me to use the MaxN algorithm with any game of my choosing, which you can find [here](https://github.com/telos-matter/Max-nJ). However only after implementing it do I unfortunately realize that I cannot use it, simply because we don't have information about the other players' current treasure, and so we can't know it's best move. And so the idea was scrapped all together, and the LookAheadPlayer was implemented instead. Which just looks for it's own best move as if he was the only player, and it works pretty well, because when it finds a way to reach the treasure in two turns for example, rarely does the other player's move totally block the path to the treasure when it's the LookAheadPlayer's turn again. 

    - An AI player was also meant to be implemented, the one for which the LocalCommunicator was meant to be used. However, I didn't have the time to implement it, and so it was left out.

## How-to:
### Requirements:
A Maven dependency is required which can be downloaded from [here](https://github.com/telos-matter/JavaUtil) and then installed locally.
### Configuration:
The client can be configured a bit trough the [config.properties](src/main/resources/config.properties) file. Such as choosing which player to use, and what not.
### Running:
After starting the server and starting the game, simply run the following command:
```bash
mvn clean compile exec:java
```

<hr>

And that's that.

A quick something: the provided server has a small bug in it that occurs after finishing the game when playing alone. The problem is that it XML schema expect there to be a maximum of 24 treasures, however, since the player's original position is also considered a treasure, when playing alone, the game has 25 treasures. And so an exception is thrown when trying to receive the final message. This can be easily fixed by changing the schema to allow for a maximum of 25 treasures.

> HEMMOUDA Aymane