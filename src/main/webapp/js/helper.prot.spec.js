describe('Zenvisage', function() {
  it('should have a title', function() {
    
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(400000);
    browser.manage().timeouts().implicitlyWait(250000);

    browser.get('http://127.0.01:80/');
    expect(browser.getTitle()).toEqual('Zenvisage');
  });
});
