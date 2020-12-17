# VendingAndroid

## About

Vending Machine implementation for Android.

# Main Activity

App get products from server and load them in the machine. Coins are preloaded.
If there are no coins for change "Out of order" message appears with Load coins button.
Load coins button resets coins.
App loads products screen.

#Products Screen

Products displayed with names and price.
If product has no quantity message "No quantity" instead of price
Reset products button resets them from server.
Click on a product leads to insert coins screen.
Back button shows toast for app exit. Click again for exit.

#Insert Coins Screen

Insert coins to meet the products price and get the product.
Cancel order with back button ot with cancel button.
If cancel app return inserted coins and displays them if any
If order app insert user coins and return and display coins as change and decrease product quantity

