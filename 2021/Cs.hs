module Cs (
    groupAt,
    lengthAtLeast,
) where

groupAt :: Int -> [a] -> [[a]]
groupAt _ [] = []
groupAt n xs
    | n <= 0 = error $ "Unexpected zero or negative length: " ++ show n
    | n > 0 = ((take n xs):groupAt n (drop n xs))

lengthAtLeast :: Int -> [Int] -> Bool
lengthAtLeast 0 _ = True
lengthAtLeast n xs
    | n < 0 = error $ "Unexpected negative length: " ++ show n
    | n > 0 = if null xs then False else lengthAtLeast (n - 1) $ tail xs
