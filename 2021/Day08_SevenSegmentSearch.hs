import AocUtil (solve)
import Cs (splitOnString)


main = do
    solve 8 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls 1 =
    sum $ map length $ map (filter isDeterministic) lineLengthsOutput
    where
        lineSideCodes = processInput ls -- [[[String]]]
        lineCodesOutput = map last lineSideCodes -- [[String]]
        lineLengthsOutput = map (map length) lineCodesOutput -- [[Int]]
        isDeterministic x = x == 2 || x == 3 || x == 4 || x == 7

processInput :: [String] -> [[[String]]]
processInput ls =
    map (\l -> map words (splitOnString " | " l)) ls
