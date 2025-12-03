require 'aoc'

function get_answer(lines, part)
    local dial = 50
    local zeros = 0
    for i, line in ipairs(lines) do
        local direction = string.sub(line, 1, 1)
        local clicks = tonumber(string.sub(line, 2))

        -- Simplify over-rotations.
        if part == 2 then
            zeros = zeros + (clicks // 100)
        end
        clicks = clicks % 100

        if direction == 'L' then
            if part == 2 and dial ~= 0 and clicks > dial then
                zeros = zeros + 1
            end
            dial = (dial + 100 - clicks) % 100
        elseif direction == 'R' then
            if part == 2 and dial ~= 0 and dial + clicks > 100 then
                zeros = zeros + 1
            end
            dial = (dial + clicks) % 100
        else
            error('Unknown direction: ' .. direction)
        end
        if dial == 0 then
            zeros = zeros + 1
        end
    end
    return zeros
end

solve(1, get_answer)
