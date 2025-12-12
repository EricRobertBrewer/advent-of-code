require 'aoc'

function get_answer(lines, part)
    local start = -1
    for j = 1, string.len(lines[1]) do
        if string.sub(lines[1], j, j) == 'S' then
            if start ~= -1 then
                error('Found multiple starting locations: ' .. start .. ' and ' .. j)
            end
            start = j
        end
    end
    if start == -1 then
        error('Unable to find starting location.')
    end

    local i = 3
    local beams = {[start] = 1}
    local n_splits = 0
    while i <= #lines do
        local line = lines[i]
        local beams_next = {}
        for j, timelines in pairs(beams) do
            local c = string.sub(line, j, j)
            if c == '^' then
                beams_next[j - 1] = (beams_next[j - 1] or 0) + timelines
                beams_next[j + 1] = (beams_next[j + 1] or 0) + timelines
                n_splits = n_splits + 1
            elseif c == '.' then
                beams_next[j] = (beams_next[j] or 0) + timelines
            else
                error('Unknown character on line `' .. i .. '` position `' .. j .. '`: ' .. c)
            end
        end
        beams = beams_next
        i = i + 2
    end

    if part == 1 then
        return n_splits
    end

    local n_timelines = 0
    for _, timelines in pairs(beams) do
        n_timelines = n_timelines + timelines
    end
    return n_timelines
end

solve(7, get_answer)
