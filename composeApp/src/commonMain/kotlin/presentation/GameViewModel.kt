package presentation

import androidx.compose.ui.graphics.Color
import model.CardState
import model.GameState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    initialGameState: GameState = GameState()
): ViewModel() {

    private val _gameState = MutableStateFlow(initialGameState)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun playerFlipCardUp(cardIndex: Int) {
        val card = _gameState.value.cards[cardIndex]

        if (card.isSelectable && card.isFlippedDown){

            val mutableCardsList = _gameState.value.cards.toMutableList()

            mutableCardsList[cardIndex] =
                card.copy(
                    isFlippedDown = false,
                    isSelected = true
                )

            _gameState.value = _gameState.value.copy(cards = mutableCardsList)

            checkForMatchAndUpdate(card)

        }
    }

    private fun flipAllCardsDown(isSelectable: Boolean = false) {
        val mutableCardsList = _gameState.value.cards.toMutableList()

        mutableCardsList.forEachIndexed { index, gameCard ->
            mutableCardsList[index] = gameCard.copy(
                isFlippedDown = true,
                isSelectable = isSelectable
            )
        }

        _gameState.update { gameState ->
            gameState.copy(
                cards = mutableCardsList
            )
        }

    }

    private fun flipAllCardsUp(isSelectable: Boolean = false) {
        val mutableCardsList = _gameState.value.cards.toMutableList()

        mutableCardsList.forEachIndexed { index, gameCard ->
            mutableCardsList[index] = gameCard.copy(
                isFlippedDown = false,
                isSelectable = isSelectable
            )
        }

        _gameState.update { gameState ->
            gameState.copy(
                cards = mutableCardsList
            )
        }

    }

    fun setupGame(
        amountOfCards: Int,
    ) {
        val cards = mutableListOf<CardState>()
        val colorsToFind = mutableListOf<Color>()
        val colors = _gameState.value.colors
        val targetColor = colors.random()

        var currentColorIndex = 0

        repeat(amountOfCards) { index ->

            val currentColor = colors[currentColorIndex]
            colorsToFind.add(currentColor)

            cards.add(
                CardState(
                    cardNumber = index + 1,
                    isFlippedDown = true,
                    color = currentColor,
                    isCorrect = currentColor == targetColor
                )
            )

            currentColorIndex = (currentColorIndex + 1) % colors.size
        }

        cards.shuffle()

        _gameState.update { gameState ->
            gameState.copy(
                cards = cards,
                targetColor = targetColor,
                colorsToFind = colorsToFind,
                points = 0,
                consecutiveMatches = 0,
                consecutiveFails = 0,
                gameLost = false,
                gameWon = false,
                gameCountdown = 1f,
                pregameCountdown = 1f,
                timedOut = false,
                threeStrikes = false
            )
        }
    }

    private fun pregameCountdown(seconds: Long = 3){

        val interval = (seconds * 1000) / 10
        viewModelScope.launch {
            for (i in 1..10) {
                delay(interval)
                _gameState.update { gameState ->
                    gameState.copy(
                        pregameCountdown = gameState.pregameCountdown - 0.1f,
                    )
                }
            }
        }
    }

    private fun startGameTimer(seconds: Long = 10){
        val interval = (seconds * 1000) / 10
        viewModelScope.launch {

            _gameState.update { gameState ->
                gameState.copy(
                    pregamePhase = false,
                    gamePhase = true,
                )
            }

            for (i in 1..10) {
                if (gameState.value.gameLost || gameState.value.gameWon){
                    break
                }
                delay(interval)
                _gameState.update { gameState ->
                    gameState.copy(
                        gameCountdown = gameState.gameCountdown - 0.1f,
                    )
                }
            }

            loseGame("timed_out")
        }
    }

    fun startGame() {
        viewModelScope.launch {
            _gameState.update { gameState ->
                gameState.copy(
                    showingStartScreen = false,
                    showingTransitionScreen = false,
                )
            }

            delay(320)

            _gameState.update { gameState ->
                gameState.copy(
                    showingGameScreen = true,
                    pregamePhase = true
                )
            }

            delay(500)

            flipAllCardsUp()

            pregameCountdown()

            delay(3000) // Wait 3s before flipping all cards down (matches countdown)

            flipAllCardsDown(isSelectable = true)

            startGameTimer()

        }

    }

    private fun updateConsecutiveMatches(){
        val consecutiveMatches = _gameState.value.consecutiveMatches + 1

        _gameState.update { gameState ->
            gameState.copy(
                points = gameState.points + (50 * consecutiveMatches),
                consecutiveMatches = consecutiveMatches,
                consecutiveFails = 0
            )
        }
    }

    private fun updateConsecutiveFails(){
        val consecutiveFails = _gameState.value.consecutiveFails + 1

        _gameState.update { gameState ->
            gameState.copy(
                perfectGame = false,
                consecutiveFails = consecutiveFails,
                consecutiveMatches = 0,
                points = gameState.points - (50 * consecutiveFails),
            )
        }
    }

    private fun loseGame(loseCondition: String = "timed_out"){
        viewModelScope.launch {
            flipAllCardsUp(isSelectable = false)

            delay(300)

            when (loseCondition){
                "timed_out" -> {
                    _gameState.update { gameState ->
                        gameState.copy(
                            perfectGame = false,
                            gameLost = true,
                            timedOut = true,
                            showingGameLostScreen = true
                            )
                    }
                }

                "three_strikes" -> {
                    _gameState.update { gameState ->
                        gameState.copy(
                            perfectGame = false,
                            gameLost = true,
                            threeStrikes = true,
                            showingGameLostScreen = true
                        )
                    }
                }

                else -> {
                    _gameState.update { gameState ->
                        gameState.copy(
                            perfectGame = false,
                            gameLost = true,
                            showingGameLostScreen = true
                        )
                    }
                }
            }
        }

    }

    private fun updateColorsToFind(lastColorRevealed : Color){
        val mutableColorsToFind = _gameState.value.colorsToFind.toMutableList()
        val targetColor = _gameState.value.targetColor

        mutableColorsToFind.remove(lastColorRevealed)

        if (!mutableColorsToFind.contains(targetColor) && mutableColorsToFind.isNotEmpty()){
            setTargetColor(mutableColorsToFind.toSet().random())
        }

        _gameState.update { gameState ->
            gameState.copy(
                colorsToFind = mutableColorsToFind
            )
        }

    }

    private fun setTargetColor(targetColor: Color) {
        _gameState.update { gameState ->
            gameState.copy(
                targetColor = targetColor
            )
        }
    }


    private fun checkForMatchAndUpdate(lastFlippedCard : CardState){

        val mutableCardsList = _gameState.value.cards.toMutableList()

        if (lastFlippedCard.isCorrect){
            updateConsecutiveMatches()
        }
        else {
            updateConsecutiveFails()
            if (_gameState.value.consecutiveFails >= 3){
                loseGame("three_strikes")
            }
        }

       updateColorsToFind(lastColorRevealed = lastFlippedCard.color)

        updateCorrectCards()

    }

    private fun updateCorrectCards() {
        val mutableCardsList = _gameState.value.cards.toMutableList()

        mutableCardsList.forEachIndexed { index, gameCard ->
            if (gameCard.isFlippedDown){
                mutableCardsList[index] = gameCard.copy(
                    isCorrect = gameCard.color == _gameState.value.targetColor
                )
            }
        }

        _gameState.update { gameState ->
            gameState.copy(
                cards = mutableCardsList
            )
        }
    }

    fun returnToMenu(){
        _gameState.update { gameState ->
            gameState.copy(
                showingStartScreen = true,
                showingGameScreen = false,
                showingGameLostScreen = false,
                showingGameWonScreen = false,
            )
        }
    }

    fun restartGame(){
        viewModelScope.launch {
            _gameState.update { gameState ->
                gameState.copy(
                    showingStartScreen = false,
                    showingGameScreen = false,
                    showingGameLostScreen = false,
                    showingGameWonScreen = false,
                    showingTransitionScreen = true
                    )
            }

            delay(300)

            setupGame(amountOfCards = 12)

            startGame()
        }
    }


}