-- https://stackoverflow.com/questions/1426954/split-string-in-lua
function split_char(s, sep)
    if sep == nil then
        sep = '%s'
    end
    local t = {}
    for match in string.gmatch(s, '([^' .. sep .. ']+)') do
        table.insert(t, match)
    end
    return t
end
