import Data.List (sort)
import qualified Data.Map as Map

import AocUtil (solve)

-- https://book.realworldhaskell.org/read/functional-programming.html
main = do
    solve 1 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls 1 =
    let (left, right) = parseInput ls
    in sum $ map distance $ zip (sort left) (sort right)
    where distance (x1, x2) = abs (x1 - x2)

getAnswer ls 2 =
    let (left, right) = parseInput ls
    in -1

parseInput :: [String] -> ([Int], [Int])
parseInput ls = unzip $ map intPair ls

intPair :: String -> (Int, Int)
intPair l = case words l of
    [s1, s2] -> (read s1, read s2)
    _ -> undefined
