import AocUtil (solve)
import Cs (lengthAtLeast)

main = do
    solve 1 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls 1 = timesIncreasing $ map read ls
getAnswer ls 2 = timesIncreasing $ windowSum 3 $ map read ls

timesIncreasing :: [Int] -> Int
timesIncreasing (a:b:xs) = (if b > a then 1 else 0) + timesIncreasing (b:xs)
timesIncreasing _ = 0

windowSum :: Int -> [Int] -> [Int]
windowSum n xs =
    if lengthAtLeast n xs
    then (sum (take n xs):windowSum n (tail xs))
    else []
