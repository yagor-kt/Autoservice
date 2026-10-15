package com.example.autoservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import com.example.autoservice.util.StatusMapper
import com.example.autoservice.util.statusColor

@Composable
fun StatusChip(status: String) {
    Text(
        text = stringResource(StatusMapper.labelRes(status)),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(statusColor(status))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 13.sp
    )
}