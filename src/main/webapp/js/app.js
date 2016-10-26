/**
 * This file initializes the app and its dependencies.
 * It also establishes the routes and views used across the app.
 */

var myApp = angular.module('zenvisageApp', ['ui.router', 'zenvisageControllers']);

myApp.config(function ($stateProvider, $urlRouterProvider) {
    // For any unmatched url, redirect to welcome page
    $urlRouterProvider.otherwise("/welcome");

    // Now set up the states
    $stateProvider
        .state('welcome', {
            url: "/welcome",
            templateUrl: "partials/welcome.html"
        })
        .state('main', {
            url: "/main",
            templateUrl: "partials/main.html"
        })
        .state('main.bar', {
            url: "/bar",
            views: {
                'canvas': {
                    templateUrl: "partials/components/canvas.html",
                    controller: "InputController"
                },
                'matches': {
                    templateUrl: "partials/components/matches.html",
                    controller: "OutputController"
                },
                'options-panel': {
                    templateUrl: "partials/components/options-panel.html",
                    controller: "InputController"
                },
                'trends': {
                    templateUrl: "partials/components/trends.html",
                    controller: "OutputController"
                },
                'zql': {
                    templateUrl: "partials/components/zql.html",
                    controller: "InputController"
                }
            }
        })
        .state('main.scatter', {
            url: "/scatter",
            views: {
                'canvas': {
                    templateUrl: "partials/components/canvas.html",
                    controller: "InputController"
                },
                'matches': {
                    templateUrl: "partials/components/matches.html",
                    controller: "OutputController"
                },
                'options-panel': {
                    templateUrl: "partials/components/options-panel.html",
                    controller: "InputController"
                },
                'trends': {
                    templateUrl: "partials/components/trends.html",
                    controller: "OutputController"
                },
                'zql': {
                    templateUrl: "partials/components/zql.html",
                    controller: "InputController"
                }
            }
        })
        .state('main.line', {
            url: "/line",
            views: {
                'canvas': {
                    templateUrl: "partials/components/canvas.html",
                    controller: "InputController"
                },
                'matches': {
                    templateUrl: "partials/components/matches.html",
                    controller: "OutputController"
                },
                'options-panel': {
                    templateUrl: "partials/components/options-panel.html",
                    controller: "InputController"
                },
                'trends': {
                    templateUrl: "partials/components/trends.html",
                    controller: "OutputController"
                },
                'zql': {
                    templateUrl: "partials/components/zql.html",
                    controller: "InputController"
                }
            }
        });
});


angular.module('zenvisageControllers', []);