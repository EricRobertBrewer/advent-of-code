module Cs (
    lengthAtLeast,
    sumFirst,
) where

lengthAtLeast :: Int -> [Int] -> Bool
lengthAtLeast 0 _ = True
lengthAtLeast _ [] = False
lengthAtLeast n xs = lengthAtLeast (n - 1) $ tail xs

sumFirst :: Int -> [Int] -> Int
sumFirst 0 _ = 0
sumFirst _ [] = 0
sumFirst n xs = head xs + (sumFirst (n - 1) (tail xs))
