# Reflection

## What I expected

I expected my recursive DFS solution to be simple, correct, and sufficient for the exercise. I assumed that collecting all song names into an ArrayList during construction would be a reasonable approach because it makes the `hasNext()` and `next()` methods straightforward.

## What actually happened

The AI suggested a different approach using a `Deque` of iterators to perform DFS without recursion. Instead of collecting all songs in advance, the AI solution finds the next song only when needed and stores it temporarily.

Both approaches preserve the required DFS order, but they differ in how they manage traversal and memory.

## Most important difference

The most important difference was **eager versus lazy traversal**.

My solution performs the entire DFS in the constructor and stores all song names in an `ArrayList`. This makes `hasNext()` very simple, but requires additional memory proportional to the number of songs.

The AI solution uses a stack of iterators to maintain the DFS state. It only searches for the next song when necessary, reducing additional memory usage to approximately O(D), where D is the nesting depth of the playlists.

The lazy approach also has a cost. In my solution, `hasNext()` only checks `index < songs.size()` and has no side effects, so its correctness is easy to see. In the AI solution, `hasNext()` advances the inner iterators, and it is correct only because `nextSong` caches the song it found; without that cache, two consecutive `hasNext()` calls would skip a song. Another difference is that my iterator is a snapshot taken at construction, while the lazy iterator reflects changes made to the part of the playlist it has not reached yet.

## What I learned

I learned that implementing an Iterator is not just about returning elements in the correct order, but also about deciding when traversal should happen and how its state should be maintained.

I also learned that `hasNext()` can perform traversal internally, as long as repeated calls do not skip elements. Caching the next song ensures that the Iterator contract is preserved.

## What I changed

In my final solution, I adopted the AI's lazy DFS approach using a `Deque<Iterator<Item>>` instead of recursively collecting all songs into an `ArrayList`.

I kept the implementation simple and maintained the original `Iterator<String>` interface. I also added comments explaining how the DFS state and next-song caching work.

## What I would do next time

Next time, I would consider memory usage and traversal strategy before implementing an Iterator.

I would compare eager and lazy approaches early in the design process and test edge cases such as empty playlists, deeply nested playlists, repeated calls to `hasNext()`, and calling `next()` after the iterator is exhausted.

## Exercise-specific insight

The biggest difference was how the DFS state was stored.

In my solution, recursion handled the DFS traversal, and the results were stored in an `ArrayList`. Once construction was complete, the iterator only needed an index.

In the AI solution, the `Deque` explicitly stores the active traversal state, while `nextSong` caches the next available result.

This showed me that the same functional behavior can be implemented with different design choices, and that lazy traversal is more suitable when we want to avoid storing all results in advance.