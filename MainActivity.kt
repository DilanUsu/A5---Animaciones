package com.example.lazylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazylist.ui.theme.LazyListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyList(perfiles = listaPerfiles)
                }
            }
        }
    }
}

@Composable
fun LazyList(perfiles: List<Perfil>, modifier: Modifier = Modifier) {
    var perfilSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Perfiles",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        items(items = perfiles, key = { it.id }) { perfil ->
            TarjetaMeGusta(
                perfil = perfil,
                seleccionado = perfil.id == perfilSeleccionado,
                onSeleccionar = {
                    perfilSeleccionado =
                        if (perfilSeleccionado == perfil.id) null else perfil.id
                }
            )
        }
    }
}

@Composable
fun TarjetaMeGusta(
    perfil: Perfil,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var meGusta by rememberSaveable { mutableIntStateOf(0) }

    var expandido by rememberSaveable { mutableStateOf(false) }

    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 400),
        label = "colorFondo"
    )

    val elevacion by animateDpAsState(
        targetValue = if (seleccionado) 8.dp else 1.dp,
        label = "elevacion"
    )

    val rotacionFlecha by animateFloatAsState(
        targetValue = if (expandido) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotacionFlecha"
    )

    val escalaCorazon = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Card(
        onClick = onSeleccionar,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = elevacion)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = perfil.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = perfil.carrera,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    meGusta++
                    scope.launch {
                        escalaCorazon.animateTo(1.5f, animationSpec = tween(120))
                        escalaCorazon.animateTo(
                            1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                    }
                }) {
                    Text("Me gusta")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (meGusta > 0) "❤️" else "🤍",
                    modifier = Modifier.scale(escalaCorazon.value)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$meGusta")

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = { expandido = !expandido }) {
                    Text(if (expandido) "Ocultar" else "Ver más")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "▼", modifier = Modifier.rotate(rotacionFlecha))
                }
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = perfil.descripcion.ifBlank {
                        "Estudiante de ${perfil.carrera}. Toca la tarjeta para " +
                            "seleccionarla y presiona \"Me gusta\" si te agrada su perfil."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LazyListTheme {
        LazyList(perfiles = listaPerfiles)
    }
}
