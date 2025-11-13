package com.example.psymed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.psymed.ui.theme.PsyMedColors

@Composable
fun PsyMedCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = PsyMedColors.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        content()
    }
}

@Composable
fun PsyMedPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PsyMedColors.Primary,
            contentColor = Color.White,
            disabledContainerColor = PsyMedColors.Primary.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
    ) {
        if (loading) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(18.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun PsyMedTextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PsyMedColors.Primary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun PsyMedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PsyMedColors.Primary,
        unfocusedBorderColor = PsyMedColors.TextLight,
        focusedLabelColor = PsyMedColors.Primary,
        unfocusedLabelColor = PsyMedColors.TextSecondary,
        cursorColor = PsyMedColors.Primary,
        focusedContainerColor = PsyMedColors.PrimaryLightest,
        unfocusedContainerColor = PsyMedColors.PrimaryLightest
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it, color = PsyMedColors.TextLight) } },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = PsyMedColors.Primary
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    content: @Composable () -> Unit
) {
    val gradient = remember {
        Brush.linearGradient(
            colors = listOf(PsyMedColors.Primary, PsyMedColors.PrimaryMedium)
        )
    }
    Box(
        modifier = modifier
            .height(height)
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

