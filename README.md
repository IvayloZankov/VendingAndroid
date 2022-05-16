# Vending Machine Android

## About

Android Vending Machine implementation with MVVM, Retrofit, RxJava.

## Description

App fetches data from server and displays products.
If there are not enough coins for change "Out of order" message appears.
On product click, if there is available quantity, user is redirected to coins screen.
On coin click user insert selected coin. 
When coins added fulfil product price product is decreased and app returns change if any.
On cancel order app returns inserted coins and redirects to products.
