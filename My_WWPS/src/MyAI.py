# coding:utf-8
# ======================================================================
# FILE:        MyAI.py
#
# AUTHOR:      Abdullah Younis
#
# DESCRIPTION: This file contains your agent class, which you will
#              implement. You are responsible for implementing the
#              'getAction' function and any helper methods you feel you
#              need.
#
# NOTES:       - If you are having trouble understanding how the shell
#                works, look at the other parts of the code, as well as
#                the documentation.
#
#              - You are only allowed to make changes to this portion of
#                the code. Any changes to other portions of the code will
#                be lost when the tournament runs your code.
# ======================================================================

from Agent import Agent
from enum import Enum
import random


class MyAI(Agent):
    class self_def_action(Enum):
        GO_RIGHT = 0
        GO_DOWN = 1
        GO_LEFT = 2
        GO_UP = 3

    def __init__(self):
        # ======================================================================
        # YOUR CODE BEGINS
        # ======================================================================

        self.__self_def_action_history_list = []
        self.__future_action_list = []
        self.__agentDir = 0
        self.__agentX = 0
        self.__agentY = 0
        self.__world_dimension = 1
        self.__complete_game = False
        self.__gold_looted = False
        self.__hasArrow = True

        self.__map = {}

        # self.__self_def_action_function = {
        #     self.self_def_action.GO_RIGHT.value: self.go_right,
        #     self.self_def_action.GO_DOWN.value: self.go_down,
        #     self.self_def_action.GO_LEFT.value: self.go_left,
        #     self.self_def_action.GO_UP.value: self.go_up
        # }

        pass
        # ======================================================================
        # YOUR CODE ENDS
        # ======================================================================

    def getAction(self, stench, breeze, glitter, bump, scream):
        # ======================================================================
        # YOUR CODE BEGINS
        # ======================================================================
        # has unfinished action
        if len(self.__future_action_list) > 0:
            return self.__future_action_list.pop(0)

        # exit when goal has been finished
        if self.__complete_game:
            # agent will back to [0,0] following the shortest path
            # if self.__agentX == 0 and self.__agentY == 0:
            #    return Agent.Action.CLIMB
            return Agent.Action.CLIMB

        # tough wall
        if bump:
            self.__world_dimension = max(self.__agentX, self.__agentY)-1
            if self.__self_def_action_history_list[-1] == self.self_def_action.GO_UP:
                self.__agentY -= 1
                return self.self_def_action.GO_DOWN
            elif self.__self_def_action_history_list[-1] == self.self_def_action.GO_DOWN:
                self.__agentY += 1
                return self.self_def_action.GO_UP
            elif self.__self_def_action_history_list[-1] == self.self_def_action.GO_LEFT:
                self.__agentX += 1
                return self.self_def_action.GO_RIGHT
            elif self.__self_def_action_history_list[-1] == self.self_def_action.GO_RIGHT:
                self.__agentX -= 1
                return self.self_def_action.GO_LEFT



        if not bump:
            if self.__agentX not in self.__map:
                self.__map[self.__agentX] = {}
            if self.__agentY not in self.__map[self.__agentX]:
                self.__map[self.__agentX][self.__agentY] = 1

        # find gold
        if glitter:
            self.__gold_looted = True
            self.__complete_game = True

            self.return_exit()
            return Agent.Action.GRAB

        #
        if stench or breeze:
            self.__complete_game = True

            self.return_exit()
            if len(self.__future_action_list) == 0:
                return Agent.Action.CLIMB
            return self.__future_action_list.pop(0)

        action = random.randrange(4)
        if action == self.self_def_action.GO_UP.value:
            self.go_up()
        elif action == self.self_def_action.GO_DOWN.value:
            self.go_down()
        elif action == self.self_def_action.GO_RIGHT.value:
            self.go_right()
        elif action == self.self_def_action.GO_LEFT.value:
            self.go_left()

        return self.__future_action_list.pop(0)
        # ======================================================================
        # YOUR CODE ENDS
        # ======================================================================

    # ======================================================================
    # YOUR CODE BEGINS
    # ======================================================================

    def return_exit(self):
        def reverse_self_def_action(self_def_action):
            return (self_def_action.value + 2) % 4
        tmp_list = list(self.__self_def_action_history_list)
        for self_def_action in tmp_list:
            if reverse_self_def_action(self_def_action) == self.self_def_action.GO_UP.value:
                self.go_up()
            elif reverse_self_def_action(self_def_action) == self.self_def_action.GO_DOWN.value:
                self.go_down()
            elif reverse_self_def_action(self_def_action) == self.self_def_action.GO_RIGHT.value:
                self.go_right()
            elif reverse_self_def_action(self_def_action) == self.self_def_action.GO_LEFT.value:
                self.go_left()

        return

    def is_visted(self, p_X, p_Y):
        if self.__map[p_X][p_Y] == 1:
            return True
        else:
            return False

    def go_up(self):
        if self.__agentDir == 0:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 1:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 2:
            self.__future_action_list.append(Agent.Action.TURN_RIGHT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 3:
            self.__future_action_list.append(Agent.Action.FORWARD)

        self.__self_def_action_history_list.append(self.self_def_action.GO_UP)
        self.__agentY += 1
        self.__agentDir = 3

        return

    def go_down(self):

        if self.__agentDir == 0:
            self.__future_action_list.append(Agent.Action.TURN_RIGHT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 1:
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 2:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 3:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        self.__self_def_action_history_list.append(self.self_def_action.GO_DOWN)
        self.__agentY -= 1
        self.__agentDir = 1

        return

    def go_right(self):

        if self.__agentDir == 0:
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 1:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 2:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 3:
            self.__future_action_list.append(Agent.Action.TURN_RIGHT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        self.__self_def_action_history_list.append(self.self_def_action.GO_RIGHT)
        self.__agentX += 1
        self.__agentDir = 0

        return

    def go_left(self):

        if self.__agentDir == 0:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 1:
            self.__future_action_list.append(Agent.Action.TURN_RIGHT)
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 2:
            self.__future_action_list.append(Agent.Action.FORWARD)

        elif self.__agentDir == 3:
            self.__future_action_list.append(Agent.Action.TURN_LEFT)
            self.__future_action_list.append(Agent.Action.FORWARD)
        self.__self_def_action_history_list.append(self.self_def_action.GO_LEFT)
        self.__agentX -= 1
        self.__agentDir = 2

        return

    # ======================================================================
    # YOUR CODE ENDS
    # ======================================================================
