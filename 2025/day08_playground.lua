require 'aoc'
require 'cs'

function get_answer(lines, part)
    local boxes = {}
    local box_edge_distance = {}
    for _, line in ipairs(lines) do
        local box = {}
        for _, x_s in ipairs(split_char(line, ',')) do
            table.insert(box, tonumber(x_s))
        end
        table.insert(boxes, box)
        table.insert(box_edge_distance, {})
    end

    -- Calculate all distances.
    local distances_box_indices = {}
    for i = 1, #boxes - 1 do
        local box_a = boxes[i]
        for j = i + 1, #boxes do
            local box_b = boxes[j]
            table.insert(distances_box_indices, {euclidean_distance(box_a, box_b), i, j})
        end
    end
    local by_distance = function (a, b)
        return a[1] < b[1]
    end
    table.sort(distances_box_indices, by_distance)

    -- Connect closest pairs of boxes.
    local d = 1
    while part == 2 or d <= 1000 do
        local distance_box_indices = distances_box_indices[d]
        local distance = distance_box_indices[1]
        local i = distance_box_indices[2]
        local j = distance_box_indices[3]
        box_edge_distance[i][j] = distance
        box_edge_distance[j][i] = distance

        if part == 2 then
            local id_box_indices, id_size, box_id = graph_ids(box_edge_distance)
            if id_size[1] == #boxes then
                return boxes[i][1] * boxes[j][1]
            end
        end

        d = d + 1
    end

    local id_box_indices, id_size, box_id = graph_ids(box_edge_distance)
    local sizes = {}
    for _, size in pairs(id_size) do
        table.insert(sizes, size)
    end
    table.sort(sizes)
    local product = 1
    for i = 1, 3 do
        local size = table.remove(sizes)
        product = product * size
    end
    return product
end

function euclidean_distance(a, b)
    local sum = 0
    for i = 1, #a do
        sum = sum + (a[i] - b[i]) * (a[i] - b[i])
    end
    return math.sqrt(sum)
end

function graph_ids(box_edge_distance)
    -- Assign a unique id to each disjoint subgraph.
    local id = 0
    local id_box_indices = {}
    local id_size = {}
    local box_id = {}
    for i_target in ipairs(box_edge_distance) do
        if box_id[i_target] == nil then
            id = id + 1
            local box_indices = {}
            local size = 0
            -- Traverse the graph.
            local frontier = {i_target}
            while #frontier > 0 do
                local i = table.remove(frontier)
                if box_indices[i] == nil then
                    box_indices[i] = true
                    size = size + 1
                    box_id[i] = id
                    for i_other in pairs(box_edge_distance[i]) do
                        table.insert(frontier, i_other)
                    end
                end
            end
            id_box_indices[id] = box_indices
            id_size[id] = size
        end
    end
    return id_box_indices, id_size, box_id
end

solve(8, get_answer)
