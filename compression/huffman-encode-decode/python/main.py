import sys
from collections import defaultdict, deque
from queue import PriorityQueue
from tabulate import tabulate
import pandas as pd

from data.preproc import *

# TODO: Test all cases

class Tree:

    class BaseNode:

        count: int

        def __init__(self, v):
            self.count = v
        
        @property
        def count(self): return self.count

        @count.setter
        def count(self, v): self.count = v
    
    class LeafNode(BaseNode):

        count: int
        label: str
        parent: TreeNode # TODO: 

        def __init__(self, v, c): # v = count | c = char
            self.count = v
            self.label = c
            self.parent = None
            # todo: these extra properties can only be set when huffman tree is being decoded
            self.binary_code = ''
            self.bits = 0

        
        @property
        def count(self): return self.count

        @count.setter
        def count(self, v): self.count = v
        
        @property
        def label(self): return self.label

        @label.setter
        def label(self, c): self.label = c
        
        @property
        def parent(self): return self.parent

        @parent.setter
        def parent(self, p: TreeNode): self.parent = p

        def is_leaf(self): return True

        # todo: these extra properties can only be set when huffman tree is being decoded
        
        @property
        def binary_code(self): return self.binary_code

        @binary_code.setter
        def binary_code(self, c): self.binary_code = c
        
        @property
        def bits(self): return self.bits

        @bits.setter
        def bits(self, b): self.bits = b

    class TreeNode(BaseNode):

        count: int
        parent: TreeNode # TODO: 
        left_child: BaseNode
        right_child: BaseNode
        left_code: int = 0
        right_code: int = 1
        
        def __init__(self, v, l: BaseNode = None, r: BaseNode = None):
            self.count = v
            self.parent = None
            self.left_child = l
            self.right_child = r
        
        @property
        def count(self): return self.count

        @count.setter
        def count(self, v): self.count = v
        
        @property
        def parent(self): return self.parent

        @parent.setter
        def parent(self, p: TreeNode): self.parent = p
        
        @property
        def left_child(self): return self.left_child

        @left_child.setter
        def left_child(self, l: BaseNode): self.left_child = l
        
        @property
        def right_child(self): return self.right_child

        @right_child.setter
        def right_child(self, r: BaseNode): self.right_child = r

        def is_leaf(self): return False
        
        # @property # todo: not required, because you only want a getter in this case
        def left_code(self): return self.left_code
        
        def right_code(self): return self.right_code


    def __init__(self): pass
    
    @classmethod
    def print_tree(tree: BaseNode, indent='', last=True):
        print(indent, end='')
        if last:
            print('└── ', end='')
            indent += '    '
        else:
            print('├── ', end='')
            indent += '│   '

        print(tree.count)

        # Recursively print each child with appropriate indentation
        tree_children = [] if tree.is_leaf() else [tree.left_child, tree.right_child]
        child_count = len(tree_children)
        for i, child in enumerate(tree_children):
            Tree.print_tree(child, indent, i == child_count - 1)


def parse_string_to_dict(s): # O(n) t | O(n) s
    if len(s or '') == 0: return {}
    char_counts = {}
    for char in s:
        if char in char_counts: char_counts[char] += 1
        else: char_counts[char] = 0
    return char_counts


def parse_string_to_defaultdict(s): # O(n) t | O(n) s
    if len(s or '') == 0: return {}
    # defaultdict simplifies code and avoids the need for explicit initialization of counts for each character
    char_counts = defaultdict(int)
    for char in s: # char_counts[char] is initialized by 0 by default
        char_counts[char] += 1 # in case char to be accessed by defaultdict wasn't found, 0 would be returned, and not KeyError
    return char_counts


def parse_dict_to_priority_queue(d): # O(n) t | O(n) s
    if len(d or {}) == 0: return None
    pq = PriorityQueue(len(d))
    for k, v in d.items(): # k = char | v = count
        node = form_node((v, k)) # count -> char
        pq.put((node.count, node))
    return pq


def form_node(tuple_node) -> Tree.LeafNode: # 1 tuple to 1 leaf node
    return Tree.LeafNode(tuple_node[0], tuple_node[1])


def form_tree(node: Tree.LeafNode, next: Tree.LeafNode) -> Tree.TreeNode: # 2 tuples (leaf nodes) to 1 tree node
    tree = Tree.TreeNode(node.count + next.count)
    node.parent = tree; next.parent = tree
    tree.left_child = node; tree.right_child = next
    return tree


def grow_tree(tree: Tree.TreeNode, node: Tree.LeafNode) -> Tree.TreeNode: # 1 (tuple) leaf node to 1 tree's root node
    new_node = None; next_node = None
    if node.count <= tree.count:
        new_node = node; next_node = tree
    else: new_node = tree; next_node = node
    new_tree = Tree.TreeNode(node.count + tree.count)
    new_node.parent = new_tree; next_node.parent = new_tree
    tree.left_child = new_node; tree.right_child = next_node
    return new_tree


def merge_trees(tree: Tree.TreeNode, next: Tree.TreeNode) -> Tree.TreeNode: # 2 trees to 1 tree
    new_node = None; next_node = None
    if tree.count <= next.count:
        new_node = tree; next_node = next
    else: new_node = next; next_node = tree
    new_tree = Tree.TreeNode(tree.count + next.count)
    new_node.parent = new_tree; next_node.parent = new_tree
    new_tree.left_child = new_node; new_tree.right_child = next_node
    return new_tree


def form_tree_from_list(arr) -> Tree.TreeNode:
    if len(arr or []) != 2: return None, None
    node = arr[0]; next = arr[1]
    if len(node or []) != 2 or len(next or []) != 2: return None, None
    if type(node[1]) is not Tree.BaseNode or type(next[1]) is not Tree.BaseNode: return None, None
    node: Tree.BaseNode = node[1]; next: Tree.BaseNode = next[1]; tree: Tree.TreeNode = None
    
    if node.is_leaf() and next.is_leaf(): 
        tree = form_tree(node, next) # 2 LeafNodes to form 1 TreeNode
    elif not node.is_leaf() and next.is_leaf():
        tree = grow_tree(node, next) # 1 TreeNode (node) growing with 1 LeafNode (next)
    elif node.is_leaf() and not next.is_leaf():
        tree = grow_tree(next, node) # 1 TreeNode (next) growing with 1 LeafNode (node)
    elif not node.is_leaf() and not next.is_leaf():
        tree = merge_trees(node, next) # 2 TreeNodes merged into 1 TreeNode

    return tree, [].copy()


def parse_priority_queue_to_tree(pq): 
    # TODO: parse priority queue into a regular tree
    pass


def parse_priority_queue_to_huffman_tree(pq): # todo: O(?) t | O(?) s

    def get_count(node):
        if type(node) is tuple and len(node) == 2:
            if type(node[1]) is str: return node[0]
            elif type(node[1]) is Tree.BaseNode: return node[1].count
        return None

    if pq is None or type(pq) is not PriorityQueue or (type(pq) is PriorityQueue and pq.empty()): return None
    node = pq.get(); next = pq.get()
    tree: Tree.TreeNode = form_tree(node, next)
    pq.put((tree.count, tree))
    arr = []
    while not pq.empty():
        if tree is None or arr is None:
            print("Error")
            return None
        if len(pq.queue) == 1: return pq.get()
        # case for 2 or more items in pq
        next = pq.get()
        if len(arr) < 2: arr.append(next)
        else:
            tree, arr = form_tree_from_list(arr)
            pq.put((tree.count, tree))
    return None


def encode_string_to_huffman_tree(s): 
    # d = parse_string_to_dict(s)
    d = parse_string_to_defaultdict(s)
    pq = parse_dict_to_priority_queue(d)
    # t = parse_priority_queue_to_tree(pq)
    ht = parse_priority_queue_to_huffman_tree(pq)
    return ht # todo: t


def cfs(node: Tree.BaseNode, code=None, cb=None): # todo: 'Code-First Search': just traversing tree by following binary code patterns of nodes & children nodes
    if code is None:
        print('No binary code')
        return None
    elif node is not None and node.is_leaf(): 
        if cb is not None: cb(node, code)
        return node
    elif len(code or '') > 0:
        # todo: return the found node, and remaining code, if any
        c = code.pop(0)
        if c == '0': cfs(node.left_child, code)
        elif c == '1': cfs(node.right_child, code)
    else: 
        print('Check for error')
        return node


def dfs(node: Tree.BaseNode, type=None, value=None, cb=None):
    if node is None: return

    if type or '' == 'Code' and len(value or '') > 0:
        return cfs(node, code=value)

    # DFS pre-order

    if node.is_leaf():
        print(f"Leaf Node: {node.label}")
        if cb is not None: cb(node)
        if type is not None and value is not None:
            if type == 'Character':
                if value == node.label:
                    return node
            elif type == 'Code':
                _, updated_node = find_tree_leaf_node_info(node)
                if updated_node is not None and value == updated_node.binary_code or '':
                    return updated_node
    else:
        dfs(node.left_child)
        dfs(node.right_child)


def bfs(node: Tree.BaseNode, type=None, value=None, cb=None):
    if node is None: return

    if type or '' == 'Code' and len(value or '') > 0:
        return cfs(node, code=value)

    queue = deque()
    queue.append(node)

    while queue:
        node = queue.popleft()
        print(node.count)

        if node.is_leaf():
            print(f"Leaf Node: {node.label}")
            if cb is not None: cb(node)
            if type is not None and value is not None:
                if type == 'Character':
                    if value == node.label:
                        return node
                elif type == 'Code':
                    _, updated_node = find_tree_leaf_node_info(node)
                    if updated_node is not None and value == updated_node.binary_code or '':
                        return updated_node
        else:
            queue.append(node.left_child)
            queue.append(node.right_child)


def traverse_tree(tree: Tree.TreeNode, method='dfs', type=None, value=None, cb=None):
    if method == 'dfs': dfs(tree, type=type, value=value, cb=cb)
    elif method == 'bfs': bfs(tree, type=type, value=value, cb=cb)
    elif method == 'cfs': cfs(tree, code=value, cb=cb) # Code-first search


def find_tree_leaf_node_info(node):
    arr = []; code = ''; i = 0
    arr.append(node.label or '')
    arr.append(node.count or '')
    while node.parent is not None:
        '''
        # todo: wrong way, as both children could have the same count value
        if node.count == node.parent.left_child.count: code += f"{0}"
        elif node.count == node.parent.right_child.count: code += f"{1}"
        '''
        # compare the Tree subclass objects by both object itself & identifier specifically
        if node is node.parent.left_child and id(node) == id(node.parent.left_child): 
            code += f"{0}"
        elif node is node.parent.right_child and id(node) == id(node.parent.right_child): 
            code += f"{1}"
        i += 1
    node.binary_code = code[::-1]
    node.bits = i
    arr.append(code[::-1])
    arr.append(i)
    return arr, node


def parse_huffman_tree_to_prefix_table(tree): 
    # parse huffman tree to prefix table, for each letter in each LeafNode
    cols = ["Character", "Frequency", "Code", "Bits"]
    pt = pd.DataFrame({ col: [] for col in cols })
    # pt.set_index("Character", inplace=True) # todo: test finding specific row with Character as the primary key index
    table = []

    def cb(node): 
        node_info, _ = find_tree_leaf_node_info(node) 
        table.append(node_info) # append new node_info to table, for tabulate() initial test
        pt.loc[len(pt.index)] = node_info # append new node_info to dataframe

    # todo: test both dfs & bfs
    traverse_tree(tree, method='dfs', cb=cb)
    # traverse_tree(tree, method='bfs', cb=cb)

    table = tabulate(table, headers=cols, tablefmt="grid")
    # table = tabulate(pt, headers="keys", tablefmt="html") # todo: also try tabulating dataframe
    print(table)

    return pt


def search_huffman_tree(tree, char=None, code=None, cb=None):
    meth = None; type = None; val = None
    if char is not None: meth = 'dfs'; type = 'Character'; val = char
    elif code is not None: meth = 'cfs'; type = 'Code'; val = code
    if meth is None or type is None or val is None:
        print('Error searching huffman tree')
        return
    found_node = traverse_tree(tree, method=meth, type=type, value=val, cb=cb)
    if found_node is not None:
        print(f"Found Node: {found_node.label or 'Not Found'}\nFrequency: {found_node.count or '-'}")
        if cb is not None: cb(found_node)

    return found_node


def search_prefix_table(pt, char=None, code=None):
    col = None; val = None
    if char is not None: col = 'Character'; val = char
    elif code is not None: col = 'Code'; val = code
    if col is None or val is None:
        print('Error searching prefix table')
        return
    
    # TODO: 1st, check if row exists based on pt[col] == val before returning it, else return None
    
    row = pt.loc[pt[col] == val].iloc[0] # or None
    # row = pt.loc[val] # todo: test finding specific row with Character as the primary key index
    if row is not None: return row.values
    else: return None


def decode_huffman_tree_with_prefix_table(tree): 
    # decode huffman tree using already built prefix table, for each letter in each LeafNode
    pt = parse_huffman_tree_to_prefix_table(tree)

    def cb(node): 
        found_node = search_huffman_tree(tree, char=node.label)
        print(f"Cross-checking Node: {found_node.label or 'Not Found'}\nFrequency: {found_node.count or '-'}")
        print()
        node_info, updated_node = find_tree_leaf_node_info(node) 
        pt_row = search_prefix_table(pt, node.label)
        # now, check node, to compare with both node_info and the found row in the prefix table, pt
        cols = ["Character", "Frequency", "Code", "Bits"]
        for i, col in enumerate(cols):
            val = ''
            print()
            if col is 'Character': 
                val = node.label
                print(f"Node Character Label: {val}")
            elif col is 'Frequency': 
                val = node.count
                print(f"Node Frequency/Count: {val}")
            elif col is 'Code': 
                val = updated_node.binary_code
                print(f"Updated Node Code Label: {val or ''}")
            elif col is 'Bits': 
                val = updated_node.bits
                print(f"Updated Node Bits: {val or ''}")
            print(f"Node in Tree for Property `{col}`: {node_info[i]}")
            print(f"Row in Prefix Table for column `{col}`: {pt_row[i]}")
            print()
            print(f"Validation Check (Actual Node Property = Tree Node Property = Prefix Table Column ?) - { \
                val == node_info[i] == pt_row[i] \
            }")
            print()

    traverse_tree(tree, method='dfs', cb=cb)


def output_headers_to_file(ht, pt, file_path=None):
    # write output headers (both huffman tree and prefix table) to file (use delimiter to separate both huffman tree and prefix tree headers)
    write_output(f"\n{'----------' * 10}\n", file_path)
    append_output(f"\nHEADER INFO - HUFFMAN TREE\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)
    append_output(f"\n{Tree.print_tree(ht)}\n", file_path)
    append_output(f"\n{'----------' * 10}\n\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)
    append_output(f"\nHEADER INFO - PREFIX TABLE\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)
    append_output(f"\n{pt.to_string()}\n", file_path)
    append_output(f"\n{'----------' * 10}\n\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)
    append_output(f"\nHEADER INFO - END\n", file_path)
    append_output(f"\n{'----------' * 10}\n\n", file_path)


def encode_string_to_binary_code_with_huffman_tree(s, tree): 
    # for each char in string, find binary code from huffman tree (with dfs)
    encoded_string = ''

    def cb(found_node): 
        print(f"Found Node: {found_node.label or 'Not Found'}\nFrequency: {found_node.count or '-'}")
        node_info, updated_node = find_tree_leaf_node_info(found_node) 
        # todo: Binary code is the 3rd item in the node_info array, or the updated_node.binary_code | .bits newly set properties
        if updated_node.binary_code or '' == node_info[2]: 
            print('Updated Node & Node info array match')
        else: print('Updated Node & Node info array DO NOT match')
        encoded_string += f"{updated_node.binary_code or node_info[2] or ''}"

    for c in s.split(): search_huffman_tree(tree, char=c, cb=cb)

    return encoded_string


def encode_string_to_binary_code_with_prefix_table(s, pt): 
    # for each char in string, find binary code from prefix table
    encoded_string = ''; found_node_info = None

    for c in s.split(): 
        found_node_info = search_prefix_table(pt, c)
        print(f"Found Node Info: {found_node_info}")
        encoded_string += f"{found_node_info[2] or ''}"
    
    return encoded_string


def compress_binary_code_to_byte_data(s):
    # TODO: pack binary code / bit strings into bytes to achieve the compression
    pass


def output_binary_code_to_file(s, file_path=None):
    # write output binary code string to file (use delimiter first, to separate from headers)
    write_output(f"\n{s}\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)


def output_byte_data_to_file(s, file_path=None):
    # write output binary code string to file (use delimiter first, to separate from headers)
    write_output(f"\n{s}\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)


def rebuild_huffman_tree_from_output_headers(file_path=None):
    # TODO: read huffman tree header information from encoded output file, then rebuild huffman tree, ready to decode the compressed text or byte data
    pass


def rebuild_prefix_tree_from_output_headers(file_path=None):
    # TODO: read prefix tree header information from encoded output file, then rebuild prefix tree, ready to decode the compressed text or byte data
    pass


def decompress_byte_data_from_output_text_to_binary_code(file_path=None):
    # TODO: read remainder (encoded bit string or binary code), using either huffman tree or prefix table
    pass


def decode_binary_code_with_huffman_tree(ht, code):
    text = ''

    # todo: in this case, 'Code-first Search' is used by default (these are the args)
    def cb(node: Tree.LeafNode=None, code=None):
        if node is not None and node.is_leaf():
            text += node.label
        if len(code or '') > 0:
            search_huffman_tree(ht, code=code, cb=cb)
    # todo: return value (found_node) not required, due to callback (in this case only)
    _ = search_huffman_tree(ht, code=code, cb=cb)
    
    print(f"Decoded Text: `{text}`")
    return text


def decode_binary_code_with_prefix_table(pt, code):
    text = ''; c = ''

    while len(code or '') > 0:
        c += code.pop(0)
        pt_row = search_prefix_table(pt, code=c)
        if pt_row is not None:
            text += pt_row[0] # take out 1st 'Character' column
    
    print(f"Decoded Text: `{text}`")
    return text


def decode_binary_code_from_output_text_to_string(file_path=None):
    # TODO: read remainder (encoded bit string or binary code), using either huffman tree or prefix table
    pass


def output_decoded_text_to_file(s, file_path='./data/new_output.txt'):
    # write output decoded text to a new output file
    write_output(f"\n{s}\n", file_path)
    append_output(f"\n{'----------' * 10}\n", file_path)


def compare_text(s1, s2):
    # TODO: compare lengths of (raw | encoded | decoded) text
    pass


def compare_files(f1, f2):
    # TODO: compare file sizes of input & output files containing (raw | encoded | decoded) text
    pass




def huffman_encode_decode(func, in_file, out_file): pass




if __name__ == '__main__': # path to input & output files as cli args
    in_file = out_file = ''
    if len(sys.argv) < 3:
        in_file = input('Enter path to input file: ')
        out_file = input('Enter path to input file: ')
    else:
        in_file = sys.argv[1]; out_file = sys.argv[2]
    func = input('`encode` or `decode` (type exact answer; default is `encode`): ')
    if func not in ['encode', 'decode']: func = 'encode'
    huffman_encode_decode(func, in_file, out_file)

