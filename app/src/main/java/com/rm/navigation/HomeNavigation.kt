package com.rm.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.data.resource.ResourceItemList
import com.rm.utils.Constants
import com.rm.view.project.ComponentAddProjectScreen
import com.rm.view.project.ComponentProjectDetailsScreen
import com.rm.view.project.ComponentProjectScreen
import com.rm.view.resource.ComponentAddResourceScreen
import com.rm.view.resource.ComponentResourceDetailsScreen
import com.rm.view.resource.ComponentResourceScreen
import com.rm.view.resource.ComponentResourceSearch

@Composable
fun HomeNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Route.HomeNav.Project.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        /* Project Screens */
        composable(route = Route.HomeNav.Project.route) {
            ComponentProjectScreen(
                navigateToProjectDetails = { route, project ->
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set(Constants.ARGUMENT_PROJECT_ID, project.parentProjectId)
                    }
                    navController.navigate(route)
                },
                navigateToAddProject = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(route = Route.HomeNav.ProjectDetails.route) {
            var projectId =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>(Constants.ARGUMENT_PROJECT_ID)
            // get resourceId back from EditResource
            if (navController.currentBackStackEntry?.savedStateHandle?.contains(Constants.ARGUMENT_PROJECT_EDITED) == true) {
                projectId =
                    navController.currentBackStackEntry?.savedStateHandle?.get<String>(Constants.ARGUMENT_PROJECT_EDITED)
            }
            ComponentProjectDetailsScreen(projectId = projectId,
                navigateToAddProject = { route, editEnable, projectDetails ->
                    navController.currentBackStackEntry?.arguments?.putBoolean(
                        Constants.ARGUMENT_PROJECT_EDIT_ENABLED,
                        editEnable
                    )
                    navController.currentBackStackEntry?.arguments?.putParcelable(
                        Constants.ARGUMENT_PROJECT,
                        projectDetails
                    )
                    navController.navigate(route)
                },
                onBackPress = { route ->
                    navController.navigate(route) {
                        popUpTo(Route.HomeNav.ResourceDetails.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Route.HomeNav.AddProject.route) {
            val editEnable =
                navController.previousBackStackEntry?.arguments?.getBoolean(
                    Constants.ARGUMENT_PROJECT_EDIT_ENABLED,
                    false
                )

            val projectDetails =
                navController.previousBackStackEntry?.arguments?.getParcelable<ProjectDetailsResponseData>(
                    Constants.ARGUMENT_PROJECT
                )
            ComponentAddProjectScreen(editEnable, projectDetails,
                onBackPress = { route ->
                    navController.navigate(route) {
                        popUpTo(Route.HomeNav.AddProject.route) {
                            inclusive = true
                        }
                    }
                }, onEditBackPress = { projectId, route ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Constants.ARGUMENT_PROJECT_EDITED,
                        projectId
                    )
                    navController.popBackStack()
                }
            )
        }


        /* Resource Screens */
        composable(route = Route.HomeNav.Resource.route) {
            ComponentResourceScreen(
                navigateToResourceDetails = { route, resource ->
                    navController.currentBackStackEntry?.arguments?.putString(
                        Constants.ARGUMENT_RESOURCE_ID,
                        resource.id
                    )
                    navController.navigate(route)
                },
                navigateToAddResource = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(route = Route.HomeNav.ResourceSearch.route) {
            ComponentResourceSearch(
                navigateToResourceDetails = { route, resource ->
                    navController.currentBackStackEntry?.arguments?.putString(
                        Constants.ARGUMENT_RESOURCE_ID,
                        resource.id
                    )
                    navController.navigate(route)
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Route.HomeNav.ResourceDetails.route) {
            var resourceId =
                navController.previousBackStackEntry?.arguments?.getString(Constants.ARGUMENT_RESOURCE_ID)

            // get resourceId back from EditResource
            if (navController.currentBackStackEntry?.savedStateHandle?.contains(Constants.ARGUMENT_RESOURCE_EDITED) == true) {
                resourceId =
                    navController.currentBackStackEntry?.savedStateHandle?.get<String>(Constants.ARGUMENT_RESOURCE_EDITED)
            }

            ComponentResourceDetailsScreen(resourceId,
                navigateToAddResource = { route, editEnable, resource ->
                    navController.currentBackStackEntry?.arguments?.putBoolean(
                        Constants.ARGUMENT_RESOURCE_EDIT_ENABLED,
                        editEnable
                    )
                    navController.currentBackStackEntry?.arguments?.putParcelable(
                        Constants.ARGUMENT_RESOURCE,
                        resource
                    )
                    navController.navigate(route)
                },
                onBackPress = { route ->
                    navController.navigate(route) {
                        popUpTo(Route.HomeNav.ResourceDetails.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Route.HomeNav.AddResource.route) {
            val editEnable =
                navController.previousBackStackEntry?.arguments?.getBoolean(
                    Constants.ARGUMENT_RESOURCE_EDIT_ENABLED,
                    false
                )
            val resource =
                navController.previousBackStackEntry?.arguments?.getParcelable<ResourceItemList>(
                    Constants.ARGUMENT_RESOURCE
                )
            ComponentAddResourceScreen(editEnable, resource,
                onBackPress = { route ->
                    navController.navigate(route) {
                        popUpTo(Route.HomeNav.AddResource.route) {
                            inclusive = true
                        }
                    }
                }, onEditBackPress = { resourceItem, route ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Constants.ARGUMENT_RESOURCE_EDITED,
                        resourceItem.id
                    )
                    navController.popBackStack()
                }
            )
        }
    }
}