import { shallowMount, VueWrapper } from '@vue/test-utils';
import { AppVue } from '@/common/primary/app';

let wrapper: VueWrapper;

const wrap = () => {
  wrapper = shallowMount(AppVue);
};

describe('App', () => {
  it('should exist', () => {
    wrap();

    expect(wrapper.exists()).toBeTruthy();
  });
});
