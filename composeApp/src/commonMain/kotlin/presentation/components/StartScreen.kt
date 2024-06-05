@file:OptIn(ExperimentalResourceApi::class)

package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import colorcardsmemorygame.composeapp.generated.resources.Res
import colorcardsmemorygame.composeapp.generated.resources.start_game
import model.GameState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun StartScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    gameState: GameState,
    setupGame: (amountOfCards: Int) -> Unit = {},
    startGame: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                setupGame(12)
                startGame()
            },
        ){
            Text(text = stringResource(Res.string.start_game))
        }

    }
}