import sys
from queue import PriorityQueue
from collections import defaultdict

# TODO: Test all cases

class Tree:

    def __init__(self): pass

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
        pq.put((v, k)) # count -> char
    return pq


def form_node(node) -> Tree.LeafNode: # 1 tuple to 1 leaf node
    return Tree.LeafNode(node[0], node[1])


def form_tree(node, next) -> Tree.TreeNode: # 2 tuples (leaf nodes) to 1 tree node
    l_node = form_node(node); r_node = form_node(next)
    tree = Tree.TreeNode(l_node.count + r_node.count) # todo: l_node.count() ?
    l_node.parent(tree); r_node.parent(tree)
    tree.left_child(l_node); tree.right_child(r_node)
    return tree


def grow_tree(tree: Tree.TreeNode, node) -> Tree.TreeNode: # 1 (tuple) leaf node to 1 tree's root node
    node = form_node(node)
    l_node = None; r_node = None
    if node.count <= tree.count:
        l_node = node; r_node = tree
    else: l_node = tree; r_node = node
    new_tree = Tree.TreeNode(node.count + tree.count)
    l_node.parent(tree); r_node.parent(tree)
    tree.left_child(l_node); tree.right_child(r_node)
    return new_tree


def merge_trees(tree: Tree.TreeNode, next: Tree.TreeNode) -> Tree.TreeNode: # 2 trees to 1 tree
    l_node = None; r_node = None
    if tree.count <= next.count:
        l_node = tree; r_node = next
    else: l_node = next; r_node = tree
    new_tree = Tree.TreeNode(tree.count + next.count)
    l_node.parent(new_tree); r_node.parent(new_tree)
    new_tree.left_child(l_node); new_tree.right_child(r_node)
    return new_tree


def form_tree_from_list(arr) -> Tree.TreeNode:
    if len(arr or []) != 2: return None, None
    node = arr[0]; next = arr[1]
    if len(node or []) != 2 or len(next or []) != 2: return None, None
    tree: Tree.TreeNode = None
    if type(node[1]) is str and type(next[1]) is str:
        tree = form_tree(node, next) # 2 LeafNodes wil be formed from node & next (tuples)
    elif type(node[1]) is Tree.TreeNode and type(next[1]) is str:
        tree = grow_tree(node[1], next) # a LeafNode will be formed from next (tuple)
    elif type(node[1]) is str and type(next[1]) is Tree.TreeNode:
        tree = grow_tree(next[1], node) # a LeafNode will be formed from node (tuple)
    elif type(node[1]) is Tree.TreeNode and type(next[1]) is Tree.TreeNode:
        tree = merge_trees(node[1], next[1])

    return tree, [].copy()


def parse_priority_queue_to_tree(pq): 
    # TODO: parse priority queue into a regular tree
    pass


def parse_priority_queue_to_huffman_tree(pq): # todo: O(?) t | O(?) s

    def get_count(next):
        if type(next) is tuple and len(next) == 2:
            if type(next[1]) is str: return next[0]
            elif type(next[1]) is Tree.BaseNode: return next[1].count
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


def dfs(tree: Tree.TreeNode): pass


def bfs(tree: Tree.TreeNode): pass


def parse_huffman_tree_to_prefix_table(tree): 
    # TODO: parse huffman tree to prefix table, for each letter in each LeafNode
    pass


def decode_huffman_tree_with_prefix_table(tree): 
    # TODO: decode huffman tree using already built prefix table, for each letter in each LeafNode
    pass


def encode_string_to_binary_code_with_huffman_tree(s, tree): 
    # TODO: for each char in string, find binary code from huffman tree (with dfs), then append to output string (& file)
    pass


def encode_string_to_binary_code_with_prefix_table(s, pt): 
    # TODO: for each char in string, find binary code from prefix table, then append to output string (& file)
    pass


def compress_binary_code_to_byte_data(s):
    # TODO: pack binary code / bit strings into bytes to achieve the compression
    pass


def output_headers_to_file(file, ht, pt):
    # TODO: write output headers (both huffman tree and prefix table) to file (use delimiter to separate both huffman tree and prefix tree headers)
    pass


def output_binary_code_to_file(file, s):
    # TODO: write output binary code string to file (use delimiter first, to separate from headers)
    pass


def rebuild_huffman_tree_from_output_headers(file):
    # TODO: read huffman tree header information from encoded output file, then rebuild huffman tree, ready to decode the compressed text or byte data
    pass


def rebuild_prefix_tree_from_output_headers(file):
    # TODO: read prefix tree header information from encoded output file, then rebuild prefix tree, ready to decode the compressed text or byte data
    pass


def decode_binary_code_from_file(file):
    # TODO: read remaider (encoded bit string or binary code), using either huffman tree or prefix table
    pass


def output_decoded_text_to_file(file, s):
    # TODO: write output decoded text to a new output file
    pass


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

