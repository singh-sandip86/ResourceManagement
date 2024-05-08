package com.rm.view.resource

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.data.resource.ResourceItemList
import com.rm.data.resource.ResourceSearchModelState
import com.rm.navigation.Route
import com.rm.utils.rememberFlowWithLifecycle
import com.rm.view.resource.viewmodel.ResourceViewModel
import com.rm.view.template.SearchView

@Composable
fun ComponentResourceSearch(
    viewModel: ResourceViewModel = hiltViewModel(),
    navigateToResourceDetails: (route: String, argument: ResourceItemList) -> Unit,
    navigateBack: () -> Unit,
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity)
) {
    val masterData = mainViewModel.masterData

    LaunchedEffect(key1 = true, block = {
        masterData?.let { viewModel.getResources(mainViewModel, it) }
    })

    val userSearchModelState by rememberFlowWithLifecycle(viewModel.resourceSearchModelState)
        .collectAsState(initial = ResourceSearchModelState.Empty)

    Column(
        modifier = Modifier.background(Color.White)
    ) {
        SearchView(
            searchText = userSearchModelState.searchText,
            placeholderText = "Search Resource",
            onSearchTextChanged = { viewModel.onSearchTextChanged(it) },
            onClearClick = { viewModel.onClearClick() },
            onNavigateBack = {
                navigateBack.invoke()
            },
            matchesFound = userSearchModelState.resources.isNotEmpty()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(all = 20.dp)
            ) {
                items(userSearchModelState.resources) { resourceItem ->
                    ResourceCard(resource = resourceItem) { resource ->
                        navigateToResourceDetails.invoke(
                            Route.HomeNav.ResourceDetails.route,
                            resource
                        )
                    }
                }
            }
        }
    }
}