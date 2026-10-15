package com.example.autoservice.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.autoservice.R
import com.example.autoservice.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestSheet(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, Int, String, String) -> Unit
) {
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.add_request),
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it; error = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.plate_number)) },
                singleLine = true
            )
            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it; error = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.brand_model)) },
                singleLine = true
            )
            OutlinedTextField(
                value = vin,
                onValueChange = { vin = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.vin)) },
                singleLine = true
            )
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it.filter(Char::isDigit) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.mileage)) },
                singleLine = true
            )
            OutlinedTextField(
                value = owner,
                onValueChange = { owner = it; error = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.owner_name)) },
                singleLine = true
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.owner_phone)) },
                singleLine = true
            )

            if (error) {
                Text(
                    text = stringResource(R.string.fill_required_fields),
                    color = MaterialTheme.colorScheme.error
                )
            }

            PrimaryButton(
                text = stringResource(R.string.create),
                onClick = {
                    val parsedMileage = mileage.toIntOrNull()
                    if (
                        plate.isBlank() || brand.isBlank() || owner.isBlank() ||
                        phone.isBlank() || parsedMileage == null
                    ) {
                        error = true
                    } else {
                        onCreate(plate, brand, vin, parsedMileage, owner, phone)
                    }
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}