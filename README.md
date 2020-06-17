# Backbase Test Application

## Steps to follow

List of instructions needed to run the project:

1. Download and unzip de file attached.
2. Make sure you have Android Studio installed.
3. Open Android Studio and press File -> Open  and select the project where from the location you unzipped it.
4. Please make sure you a device connect to your laptop or an emulator opened.
5. Press Run 'app' icon from the top right toolbar or Run -> Run 'app' from the base toolbar
6. After building the application, the MainActivity with CitiesFragment will be opened with a loading screen and toolbar waiting for the input data to be parsed and sorted.
7. The fragment has the cities list a searchView on the top right toolbar which cand be used to search different prefixes in the list.
8. When you pressed a city from the list, you will be redirected in the MapFragment where you will be zoomed in to your marker location from the map.
9. This fragment has the toolbar hidden so that you can have a full view of your map.
10. When you press back, you will redirected to CitiesFragment in the same state it was left.
11. If you press the search icon, en edit text will be shown where you can enter your filter prefixes.
12. The search is quick and it uses behind the Binary Search algorithm. In the worst case scenario it will have an O(log(N)) time complexity, which is very good.
13. The minimum Android version is 5.0, according to the requirements.
14. In order to run the unit tests implemented for search algorithm, please go to src/test/java/ro/iss/backbasetest/CitiesUnitTest class, right click on it and select Run 'CitiesUnitTest'
15. I used the Android Studio 4.0.0 version

## Implementation details

On the implementation side: 
1. I've used binary search for search filtering
2. Coroutines for executing tasks that are specific mostly for background
3. MVVM as design pattern for CitiesFragment
4. RecyclerView and Adapter for dynamic list
5. LiveData as the observable data holder
6. Interface Callback for communication between Fragments and Activity
7. On the UI part I've used mainly ConstraintLayout, FrameLayout , RecyclerView and LinearLayout as containers and TextView, AppCompatImage, ContentLoadingProgressBar as views
8. I didn't use Navigation Component mostly because I had only two fragments and it was much more easier to handle the navigation via backstack
9. I didn't use dependency injection because I didn't actually need it in this case. It was much more easier and relatable to the complexity of the problem to use Singleton pattern via object classes from Kotlin. In other projects, I've used Dagger as dependency injection library.


In the process of picking the right search algorithm, I've had the following options besides Binary Search:
1. Knuth Morris Pratt Pattern Search: Time Complexity O(m+n), Space Complexity O(m)
2. Jump Search: Time Complexity O(sqrt(n)), Space Complexity O(1)
3. Interpolation Search: Time Complexity O(log(log(n))), Space Complexity O(1)
4. Exponential Search: Time Complexity O(log(n)), Space Complexity O(1)
5. Fibonacci Search: Time Complexity O(log(n)), Space Complexity O(1)
6. Trie, Radix Trie
7. NDK

From all these options, other good alternatives would have been Interpolation Search, Fibonacci Search, Trie or even NDK (I didn't really go that much through with this one, but it could have been at least a nice alternative to think about).

What are the disadvantages I saw on these options vs Binary Search:
1. Interpolation Search: the sorted data has to be uniform distributed. If not, it can get to a comparable O(n) time complexity, which is not good. It's basically a Binary Search improved but with more conditions. This premise it's a little but too risky to convince me to use it, especially when the time complexity can be liniar if it's not uniform.
2. Fibonacci Search: A good option, but it might be CPU expensive mostly because of the arithmetic involved in this algorithm. You will need more lookups to compensate for faster memory access and so on.
3. Trie/Radix Trie: Depending on the implementation, in terms of time complexity it can get really good. Unfortunately, every improvement you make for time complexity, it can increase a lot the space complexity which is not a good idea. The space complexity was the one that didn't convince to chose this implementation over Binary Search.

From my point of view, Binary Search has a stable space complexity, it's always O(1) if it's implemented in an iterative way. And the time complexity can't get worse than O(log(n)) in the worst case scenario. And it only has one condition in order to use it: the list should be sorted.