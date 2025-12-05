require 'aoc'

function get_answer(lines, part)
    local grid = {}
    for i, line in ipairs(lines) do
        local row = {}
        for j = 1, string.len(line) do
            row[j] = string.sub(line, j, j)
        end
        grid[i] = row
    end

    local rolls_total = 0
    while true do
        local grid_next = {}
        local did_access = false
        for i, row in ipairs(grid) do
            local row_next = {}
            for j, c in ipairs(grid[i]) do
                if c == '@' then
                    local rolls_adj = 0
                    for i_adj = i - 1, i + 1 do
                        if grid[i_adj] then
                            for j_adj = j - 1, j + 1 do
                                if (i_adj ~= i or j_adj ~= j) and grid[i_adj][j_adj] == '@' then
                                    rolls_adj = rolls_adj + 1
                                end
                            end
                        end
                    end
                    if rolls_adj < 4 then
                        row_next[j] = 'x'
                        rolls_total = rolls_total + 1
                        did_access = true
                    else
                        row_next[j] = '@'
                    end
                else
                    row_next[j] = c
                end
            end
            grid_next[i] = row_next
        end

        if part == 1 or not did_access then break end
        grid = grid_next
    end

    return rolls_total
end

solve(4, get_answer)
