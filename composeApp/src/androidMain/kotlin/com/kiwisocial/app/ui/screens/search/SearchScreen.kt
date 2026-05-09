package com.kiwisocial.app.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiwisocial.app.ui.screens.home.PostItem
import com.kiwisocial.app.viewModel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    currentUserId: String,
    onPostClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search") })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth().padding(paddingValues),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            when {
                query.isBlank() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Search posts to discover content.")
                }

                results.isEmpty() && !isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No results")
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(results) { result ->
                        PostItem(
                            post = result,
                            onClick = { onPostClick(result.id) },
                            onAuthorClick = { onAuthorClick(result.author.id) },
                            currentUserId = currentUserId,
                            interactions = viewModel.interactions,
                        )
                    }
                }
            }
        }
    }
}
