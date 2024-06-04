package presentation

import GameState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GameBoard(
    modifier: Modifier = Modifier,
    rows: Int = 4,
    columns: Int = 3,
    cardSize: Dp = 100.dp,
    gameState: GameState,
    onCardFlipped: (Int) -> Unit
){
    AnimatedVisibility(
        modifier = modifier,
        visible = gameState.showGameScreen,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        )
    ){

        Column(
//            modifier = modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(rows) { row ->
                Row(
                    modifier = Modifier.weight(0.25f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(columns) { column ->

                        val index = row * columns + column
                        val card = gameState.cards[index]

                        key(card.cardNumber) {
                            GameCard(
                                card = card,
                                onFlipped = { onCardFlipped(index) },
                                modifier = Modifier.weight(0.2f)
                            )
                        }
                    }
                }
            }
        }

    }

}