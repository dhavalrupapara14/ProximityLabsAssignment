# ProximityLabsAssignment

- This is a single activity app (Refer MainActivity)
- Architecture used: MVVM with repository
- WebSocket handling code is inside the WebServicesProvider and WebSocketListener.kt files
- Used Okhttp for socket connection and kotlin coroutines for background task.

### Flow and core logic:

MainActivity -> MainViewModel -> MainRepository -> WebServicesProvider -> SocketListener -> MainViewModel -> MainActivity -> AirQualityAdapter

### Steps:
1. When activity opens, we initiate socket connection and start observing for upcoming messages.
2. When a new message is received, we send it back to observer (MainViewModel)
3. We parse string into list of AirQuality objects and add/update it in the original list.
4. The original list is actually a Hash map which stores all the unique objects based on city name Map<City name, Object>
5. After updating it in the map, we send it to the MainActivity using live data and update into recycler view adapter from there.
6. In the adapter, we have used DiffUtil to calculate the difference and update the items in the recycler view with minimal effort and time.
7. This way every new message gets updated in the recycler view.
8. When activity is closed, we close the websocket and channel.

Time taken for this app is 1 day.
