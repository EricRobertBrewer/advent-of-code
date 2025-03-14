module Cs (
    lengthAtLeast,
    sumFirst,
) where

lengthAtLeast :: Int -> [Int] -> Bool
lengthAtLeast 0 _ = True
lengthAtLeast n xs
    | n < 0 = error $ "Unexpected negative length: " ++ show n
    | n > 0 = if null xs then False else lengthAtLeast (n - 1) $ tail xs

sumFirst :: Int -> [Int] -> Int
sumFirst 0 _ = 0
sumFirst n xs
    | n < 0 = error $ "Unexpected negative length: " ++ show n
    | n > 0 = head xs + (sumFirst (n - 1) (tail xs))
