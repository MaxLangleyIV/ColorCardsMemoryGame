@file:OptIn(ExperimentalResourceApi::class)

package presentation.components.gameScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import colorcardsmemorygame.composeapp.generated.resources.Res
import colorcardsmemorygame.composeapp.generated.resources.better_luck
import colorcardsmemorygame.composeapp.generated.resources.excellent_work
import colorcardsmemorygame.composeapp.generated.resources.nice_try

import colorcardsmemorygame.composeapp.generated.resources.nice_work
import colorcardsmemorygame.composeapp.generated.resources.perfect_game
import colorcardsmemorygame.composeapp.generated.resources.points
import colorcardsmemorygame.composeapp.generated.resources.restart
import colorcardsmemorygame.composeapp.generated.resources.return_to_menu
import colorcardsmemorygame.composeapp.generated.resources.well_done
import colorcardsmemorygame.composeapp.generated.resources.you_got_x_out_of_y
import model.GameState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun WinScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    gameState: GameState = GameState(),
    onRestart: () -> Unit,
    onReturnToMenu: () -> Unit

){
    val gameRatio by remember {
        mutableStateOf(
            gameState.correctChoices.toFloat() / gameState.totalCards.toFloat()
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center){
        Column(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (gameState.perfectGame){
                Text(
                    text = stringResource(Res.string.perfect_game),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
            }
            else Text(
                text =
                if (gameRatio < 0.6) { stringResource(Res.string.nice_try) }
                else if (gameRatio < 0.90) { stringResource(Res.string.well_done) }
                else { stringResource(Res.string.excellent_work) },
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(
                    Res.string.you_got_x_out_of_y,
                    gameState.correctChoices,
                    gameState.totalCards
                ),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.points, gameState.points),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = onRestart) {
                Text(text = stringResource(Res.string.restart))
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onReturnToMenu) {
                Text(text = stringResource(Res.string.return_to_menu))
            }
        }
    }
}