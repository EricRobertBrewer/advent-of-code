import Data.List (sort)
import qualified Data.Map as Map

import AocUtil (solve)
import Cs (lengthAtLeast, sumFirst)

main = do
    solve 1 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls 1 = timesIncreasing $ map read ls
getAnswer ls _ = timesIncreasing $ windowSum 3 $ map read ls

timesIncreasing :: [Int] -> Int
timesIncreasing (a:(b:xs)) = (if b > a then 1 else 0) + timesIncreasing (b:xs)
timesIncreasing _ = 0

windowSum :: Int -> [Int] -> [Int]
windowSum n xs =
    if lengthAtLeast n xs
    then (sumFirst n xs:windowSum n (tail xs))
    else []
