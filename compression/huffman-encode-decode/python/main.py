import sys

from queue import PriorityQueue
from collections import defaultdict


class Tree:

    def __init__(self): pass

    class BaseNode:

        def __init__(self, v):
            self.value = v
        
        @property
        def value(self): return self.value

        @value.setter
        def value(self, v): self.value = v
    
    class LeafNode(BaseNode):

        def __init__(self, v, c): # v = count | c = char
            self.value = v
            self.label = c
        
        @property
        def value(self): return self.value

        @value.setter
        def value(self, v): self.value = v
        
        @property
        def label(self): return self.label

        @label.setter
        def label(self, c): self.label = c

        def is_leaf(self): return True

    class TreeNode(BaseNode):
        
        def __init__(self, v, l=None, r=None): # todo: l: TreeNode | LeafNode, r: TreeNode | LeafNode):
            self.value = v
            self.left_child = l
            self.right_child = r
        
        @property
        def value(self): return self.value

        @value.setter
        def value(self, v): self.value = v
        
        @property
        def left_child(self): return self.left_child

        @left_child.setter
        def left_child(self, l): self.left_child = l
        
        @property
        def right_child(self): return self.right_child

        @right_child.setter
        def right_child(self, r): self.right_child = r

        def is_leaf(self): return False


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
    l_node = form_node(node)
    r_node = form_node(next)
    tree = Tree.TreeNode(l_node.value + r_node.value, l_node, r_node) # todo: l_node.value() ?
    return tree


def grow_tree(tree: Tree.TreeNode, node) -> Tree.TreeNode: # 1 (tuple) leaf node to 1 tree's root node
    node = form_node(node)
    l_node = None; r_node = None
    if node.value <= tree.value:
        l_node = node; r_node = tree
    else: l_node = tree; r_node = node
    new_tree = Tree.TreeNode(node.value + tree.value, l_node, r_node)
    return new_tree


def merge_trees(tree: Tree.TreeNode, next: Tree.TreeNode) -> Tree.TreeNode: # 2 trees to 1 tree
    l_node = None; r_node = None
    if tree.value <= next.value:
        l_node = tree; r_node = next
    else: l_node = next; r_node = tree
    new_tree = Tree.TreeNode(tree.value + next.value, l_node, r_node)
    return new_tree


def form_tree_from_list(arr) -> Tree.TreeNode:
    if len(arr or []) != 2: return None, None
    node = arr[0]; next = arr[1]
    if len(node or []) != 2 or len(next or []) != 2: return None, None
    tree: Tree.TreeNode = None
    if type(node[1]) is Tree.TreeNode and type(next[1]) is Tree.TreeNode:
        tree = merge_trees(node[1], next[1])
    elif type(node[1]) is Tree.TreeNode and type(next[1]) is str:
        tree = grow_tree(node[1], next) # a LeafNode will be formed from next (tuple)
    elif type(node[1]) is str and type(next[1]) is Tree.TreeNode:
        tree = grow_tree(next[1], node) # a LeafNode will be formed from node (tuple)
    elif type(node[1]) is str and type(next[1]) is str:
        tree = form_tree(node, next) # 2 LeafNodes wil be formed from node & next (tuples)

    return tree, [].copy()


def parse_priority_queue_to_tree(pq): pass


def parse_priority_queue_to_huffman_tree(pq): # todo: O(?) t | O(?) s

    def get_count_value(next):
        if type(next) is tuple and len(next) == 2:
            if type(next[1]) is str: return next[0]
            elif type(next[1]) is Tree.BaseNode: return next[1].value
        return None

    if pq is None or type(pq) is not PriorityQueue or (type(pq) is PriorityQueue and pq.empty()): return None
    node = pq.get(); next = pq.get()
    tree: Tree.TreeNode = form_tree(node, next)
    pq.put((tree.value, tree))
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
            pq.put((tree.value, tree))
    return None


def encode_string_to_huffman_tree(s): pass


def parse_huffman_tree_to_prefix_table(tree): pass


def decode_huffman_tree_with_prefix_table(tree): pass


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

