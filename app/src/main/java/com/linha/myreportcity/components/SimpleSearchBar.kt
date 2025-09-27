package com.linha.myreportcity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import com.linha.myreportcity.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    userVM: UserViewModel
) {
    val searchedUser by userVM.searchedUser.observeAsState(emptyList())
    val token by userVM.tempToken.collectAsState()
    val isLoading by userVM.isLoading.collectAsState()

    val searchResult = searchedUser
    SearchBar(
        expanded = true,
        onExpandedChange = { isExpanded ->
            if (!isExpanded) {
                onClose()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true },

        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState.text.toString(),
                onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                onSearch = {
                    onSearch(textFieldState.text.toString())
                    userVM.searchUsername(token.toString(), textFieldState.text.toString())
                },
                expanded = true,
                onExpandedChange = { },
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable(
                            onClick = {
                                onClose()
                            }
                        )
                    )
                }
            )
        },
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // search results
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (searchResult != null) {
                    searchResult.forEach { result ->
                        ListItem(
                            headlineContent = { Text(result.name) },
                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit { replace(0, length, result.name) }
                                    onClose()
                                }
                                .fillMaxWidth()
                        )
                    }
                } else {
                    Text("No results found")
                }
            }
            //--------------------- LOADING ----------------------//
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .clickable(enabled = false, onClick = {})
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}