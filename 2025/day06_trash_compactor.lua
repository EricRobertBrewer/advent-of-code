require 'aoc'
require 'cs'

function get_answer(lines, part)
    local ops = {}
    local expression_operands = {}
    if part == 1 then
        ops = split_char(lines[#lines])
        for _ in pairs(ops) do
            table.insert(expression_operands, {})
        end
        for j = 1, #lines - 1 do
            local xs_s = split_char(lines[j])
            if #xs_s ~= #ops then
                error('Unexpected number of operands on line `' .. j .. '` (expected ' .. #ops .. '): ' .. #xs_s)
            end
            for i, x_s in ipairs(xs_s) do
                table.insert(expression_operands[i], tonumber(x_s))
            end
        end
    else
        -- Read operators and positions.
        local poss_op = {}
        local line_ops = lines[#lines]
        for pos = 1, string.len(line_ops) do
            local c = string.sub(line_ops, pos, pos)
            if c ~= ' ' then
                table.insert(poss_op, pos)
                table.insert(ops, c)
            end
        end
        -- Read operands.
        for i, pos_op in ipairs(poss_op) do
            local n = i < #ops and poss_op[i + 1] - 1 - pos_op or #lines[1] - pos_op + 1
            local operands = {}
            for j = 1, n do
                table.insert(operands, 0)
            end
            for d = 1, #lines - 1 do
                for j = 1, n do
                    local digit_s = string.sub(lines[d], pos_op + j - 1, pos_op + j - 1)
                    if digit_s ~= ' ' then
                        local digit = tonumber(digit_s)
                        operands[j] = operands[j] * 10 + digit
                    end
                end
            end
            table.insert(expression_operands, operands)
        end
    end

    local grand_total = 0
    for i = 1, #ops do
        local operands = expression_operands[i]
        if ops[i] == '+' then
            local sum = 0
            for _, operand in ipairs(operands) do
                sum = sum + operand
            end
            grand_total = grand_total + sum
        elseif ops[i] == '*' then
            local product = 1
            for _, operand in ipairs(operands) do
                product = product * operand
            end
            grand_total = grand_total + product
        else
            error('Unknown operator at position `' .. j .. '`: ' .. ops[j])
        end
    end
    return grand_total
end

solve(6, get_answer)
