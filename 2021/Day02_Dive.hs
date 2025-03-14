import AocUtil (solve)

main = do
    solve 2 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls 1 =
    horizontal * depth
    where (horizontal, depth) = pilot $ course ls
getAnswer ls 2 =
    horizontal * depth
    where (horizontal, depth, aim) = pilot2 $ course ls

course :: [String] -> [(String, Int)]
course [] = []
course (l:ls) =
    case words l of
    [direction, x] -> ((direction, read x):course ls)
    _ -> undefined

pilot :: [(String, Int)] -> (Int, Int)
pilot [] = (0, 0)
pilot ((direction, x):xs) =
    -- Tail recursion; order doesn't matter.
    let (horizontal, depth) = pilot xs
    in case direction of
       "down" ->    (horizontal, depth + x)
       "up" ->      (horizontal, depth - x)
       "forward" -> (horizontal + x, depth)
       _ -> undefined

pilot2 :: [(String, Int)] -> (Int, Int, Int)
pilot2 [] = (0, 0, 0)
pilot2 xs =
    -- Head recursion; MUST traverse in order.
    let (horizontal, depth, aim) = pilot2 $ init xs
        (direction, x) = last xs
    in case direction of
       "down" ->    (horizontal, depth, aim + x)
       "up" ->      (horizontal, depth, aim - x)
       "forward" -> (horizontal + x, depth + (aim * x), aim)
       _ -> undefined
